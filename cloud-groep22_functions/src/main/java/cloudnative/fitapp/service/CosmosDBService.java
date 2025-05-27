package cloudnative.fitapp.service;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for Cosmos DB operations in Azure Functions.
 * This replaces Spring Data Cosmos since we don't have Spring context.
 */
public class CosmosDBService {

    private static CosmosDBService instance;
    private final CosmosClient cosmosClient;
    private final CosmosDatabase database;
    private final ObjectMapper objectMapper;

    private CosmosDBService() {
        String endpoint = System.getenv("AZURE_COSMOS_URI");
        String key = System.getenv("AZURE_COSMOS_KEY");
        String databaseName = System.getenv("AZURE_COSMOS_DATABASE_NAME");

        if (endpoint == null || key == null || databaseName == null) {
            throw new RuntimeException("Missing Cosmos DB configuration. Please set AZURE_COSMOS_URI, AZURE_COSMOS_KEY, and AZURE_COSMOS_DATABASE_NAME environment variables.");
        }

        this.cosmosClient = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .buildClient();

        this.database = cosmosClient.getDatabase(databaseName);

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static synchronized CosmosDBService getInstance() {
        if (instance == null) {
            instance = new CosmosDBService();
        }
        return instance;
    }

    public <T> T save(String containerName, T item, String partitionKey, Class<T> clazz) {
        try {
            CosmosContainer container = database.getContainer(containerName);
            CosmosItemResponse<T> response = container.createItem(item, new PartitionKey(partitionKey), new CosmosItemRequestOptions());
            return response.getItem();
        } catch (CosmosException e) {
            if (e.getStatusCode() == 409) {
                // Item already exists, try to replace it
                return update(containerName, item, partitionKey, clazz);
            }
            throw new RuntimeException("Error saving item to Cosmos DB", e);
        }
    }

    public <T> T update(String containerName, T item, String partitionKey, Class<T> clazz) {
        try {
            CosmosContainer container = database.getContainer(containerName);
            CosmosItemResponse<T> response = container.replaceItem(item, getItemId(item), new PartitionKey(partitionKey), new CosmosItemRequestOptions());
            return response.getItem();
        } catch (CosmosException e) {
            throw new RuntimeException("Error updating item in Cosmos DB", e);
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
            CosmosContainer container = database.getContainer(containerName);
            CosmosPagedIterable<T> items = container.readAllItems(new PartitionKey(""), clazz);
            return items.stream().collect(Collectors.toList());
        } catch (CosmosException e) {
            throw new RuntimeException("Error reading items from Cosmos DB", e);
        }
    }

    public <T> List<T> query(String containerName, String sqlQuery, Class<T> clazz) {
        try {
            CosmosContainer container = database.getContainer(containerName);
            CosmosPagedIterable<T> items = container.queryItems(sqlQuery, new CosmosQueryRequestOptions(), clazz);
            return items.stream().collect(Collectors.toList());
        } catch (CosmosException e) {
            throw new RuntimeException("Error querying Cosmos DB", e);
        }
    }

    public void deleteById(String containerName, String id, String partitionKey) {
        try {
            CosmosContainer container = database.getContainer(containerName);
            container.deleteItem(id, new PartitionKey(partitionKey), new CosmosItemRequestOptions());
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
}