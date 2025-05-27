package cloudnative.fitapp.service;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service for Cosmos DB operations in Azure Functions.
 * This replaces Spring Data Cosmos since we don't have Spring context.
 */
public class CosmosDBService {

    private static final Logger logger = Logger.getLogger(CosmosDBService.class.getName());
    private static CosmosDBService instance;
    private final CosmosClient cosmosClient;
    private final CosmosDatabase database;
    private final ObjectMapper objectMapper;

    private CosmosDBService() {
        String endpoint = System.getenv("AZURE_COSMOS_URI");
        String key = System.getenv("AZURE_COSMOS_KEY");
        String databaseName = System.getenv("AZURE_COSMOS_DATABASE_NAME");

        logger.info("Initializing Cosmos DB Service...");
        logger.info("Endpoint: " + endpoint);
        logger.info("Database: " + databaseName);

        if (endpoint == null || key == null || databaseName == null) {
            String errorMsg = "Missing Cosmos DB configuration. Please set AZURE_COSMOS_URI, AZURE_COSMOS_KEY, and AZURE_COSMOS_DATABASE_NAME environment variables.";
            logger.severe(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        this.cosmosClient = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .buildClient();

        this.database = cosmosClient.getDatabase(databaseName);

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        logger.info("Cosmos DB Service initialized successfully");
    }

    public static synchronized CosmosDBService getInstance() {
        if (instance == null) {
            instance = new CosmosDBService();
        }
        return instance;
    }

    public <T> T save(String containerName, T item, String partitionKey, Class<T> clazz) {
        try {
            logger.info("Saving item to container: " + containerName + " with partition key: " + partitionKey);

            CosmosContainer container = database.getContainer(containerName);
            logger.info("Got container reference");

            // Debug: log the item being saved
            logger.info("Item to save: " + objectMapper.writeValueAsString(item));

            CosmosItemResponse<T> response = container.createItem(item, new PartitionKey(partitionKey), new CosmosItemRequestOptions());
            logger.info("Save response status: " + response.getStatusCode());

            T savedItem = response.getItem();
            logger.info("Saved item: " + (savedItem != null ? "success" : "null"));

            return savedItem;
        } catch (CosmosException e) {
            logger.severe("CosmosException during save: " + e.getMessage());
            logger.severe("Status code: " + e.getStatusCode());
            logger.severe("Error details: " + e.getMessage());

            if (e.getStatusCode() == 409) {
                logger.info("Item already exists, trying to replace it");
                // Item already exists, try to replace it
                return update(containerName, item, partitionKey, clazz);
            }
            throw new RuntimeException("Error saving item to Cosmos DB: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.severe("General exception during save: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving item to Cosmos DB", e);
        }
    }

    public <T> T update(String containerName, T item, String partitionKey, Class<T> clazz) {
        try {
            logger.info("Updating item in container: " + containerName);

            CosmosContainer container = database.getContainer(containerName);
            String itemId = getItemId(item);
            logger.info("Updating item with ID: " + itemId);

            CosmosItemResponse<T> response = container.replaceItem(item, itemId, new PartitionKey(partitionKey), new CosmosItemRequestOptions());
            logger.info("Update response status: " + response.getStatusCode());

            return response.getItem();
        } catch (CosmosException e) {
            logger.severe("CosmosException during update: " + e.getMessage());
            throw new RuntimeException("Error updating item in Cosmos DB: " + e.getMessage(), e);
        }
    }

    public <T> Optional<T> findById(String containerName, String id, String partitionKey, Class<T> clazz) {
        try {
            CosmosContainer container = database.getContainer(containerName);
            CosmosItemResponse<T> response = container.readItem(id, new PartitionKey(partitionKey), clazz);
            return Optional.of(response.getItem());
        } catch (CosmosException e) {
            if (e.getStatusCode() == 404) {
                return Optional.empty();
            }
            throw new RuntimeException("Error reading item from Cosmos DB", e);
        }
    }

    public <T> List<T> findAll(String containerName, Class<T> clazz) {
        try {
            logger.info("Finding all items in container: " + containerName);
            CosmosContainer container = database.getContainer(containerName);

            // Use a proper SQL query instead of readAllItems
            String query = "SELECT * FROM c";
            CosmosPagedIterable<T> items = container.queryItems(query, new CosmosQueryRequestOptions(), clazz);

            List<T> result = items.stream().collect(Collectors.toList());
            logger.info("Found " + result.size() + " items");
            return result;
        } catch (CosmosException e) {
            logger.severe("Error reading all items from Cosmos DB: " + e.getMessage());
            throw new RuntimeException("Error reading items from Cosmos DB", e);
        }
    }

    public <T> List<T> query(String containerName, String sqlQuery, Class<T> clazz) {
        try {
            logger.info("Executing query on container " + containerName + ": " + sqlQuery);
            CosmosContainer container = database.getContainer(containerName);
            CosmosPagedIterable<T> items = container.queryItems(sqlQuery, new CosmosQueryRequestOptions(), clazz);

            List<T> result = items.stream().collect(Collectors.toList());
            logger.info("Query returned " + result.size() + " items");
            return result;
        } catch (CosmosException e) {
            logger.severe("Error querying Cosmos DB: " + e.getMessage());
            throw new RuntimeException("Error querying Cosmos DB", e);
        }
    }

    public void deleteById(String containerName, String id, String partitionKey) {
        try {
            CosmosContainer container = database.getContainer(containerName);
            container.deleteItem(id, new PartitionKey(partitionKey), new CosmosItemRequestOptions());
            logger.info("Deleted item with ID: " + id);
        } catch (CosmosException e) {
            if (e.getStatusCode() != 404) { // Ignore not found errors
                throw new RuntimeException("Error deleting item from Cosmos DB", e);
            }
        }
    }

    public long count(String containerName) {
        try {
            CosmosContainer container = database.getContainer(containerName);
            String query = "SELECT VALUE COUNT(1) FROM c";
            CosmosPagedIterable<Integer> result = container.queryItems(query, new CosmosQueryRequestOptions(), Integer.class);
            return result.iterator().next().longValue();
        } catch (CosmosException e) {
            throw new RuntimeException("Error counting items in Cosmos DB", e);
        }
    }

    private String getItemId(Object item) {
        try {
            // Use reflection to get the id field
            var field = item.getClass().getDeclaredField("id");
            field.setAccessible(true);
            return (String) field.get(item);
        } catch (Exception e) {
            throw new RuntimeException("Error getting item ID", e);
        }
    }

    public void close() {
        if (cosmosClient != null) {
            cosmosClient.close();
        }
    }

    // Add a method to ensure containers exist
    public void ensureContainersExist() {
        try {
            logger.info("Ensuring containers exist...");

            // Create users container if it doesn't exist
            CosmosContainerProperties usersContainer = new CosmosContainerProperties("users", "/email");
            database.createContainerIfNotExists(usersContainer, ThroughputProperties.createManualThroughput(400));
            logger.info("Users container ready");

            // Create workouts container if it doesn't exist
            CosmosContainerProperties workoutsContainer = new CosmosContainerProperties("workouts", "/userId");
            database.createContainerIfNotExists(workoutsContainer, ThroughputProperties.createManualThroughput(400));
            logger.info("Workouts container ready");

        } catch (Exception e) {
            logger.severe("Error ensuring containers exist: " + e.getMessage());
            e.printStackTrace();
        }
    }
}