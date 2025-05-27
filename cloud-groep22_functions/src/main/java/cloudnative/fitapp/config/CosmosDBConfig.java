package cloudnative.fitapp.config;

import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Azure Cosmos DB connection.
 * This class sets up the connection to your existing Cosmos DB instance.
 */
@Configuration
@EnableCosmosRepositories(basePackages = "cloudnative.fitapp.repository")
public class CosmosDBConfig extends AbstractCosmosConfiguration {

    @Value("${AZURE_COSMOS_URI}")
    private String uri;

    @Value("${AZURE_COSMOS_KEY}")
    private String key;

    @Value("${AZURE_COSMOS_DATABASE_NAME:cloud-native-groep22-db}")
    private String dbName;

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    /**
     * Cosmos DB configuration with performance settings.
     * These settings optimize for Azure Functions workload.
     */
    @Bean
    public CosmosConfig cosmosConfig() {
        return CosmosConfig.builder()
                .enableQueryMetrics(true)
                .maxDegreeOfParallelism(1000)
                .maxBufferedItemCount(100)
                .build();
    }

    // These methods provide the connection details
    public String getUri() {
        return uri;
    }

    public String getKey() {
        return key;
    }
}