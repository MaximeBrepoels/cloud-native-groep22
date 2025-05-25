package cloudnative.fitapp.config;

import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCosmosRepositories(basePackages = "cloudnative.fitapp.repository")
public class CosmosConfiguration extends AbstractCosmosConfiguration {

    @Value("${spring.cloud.azure.cosmos.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.azure.cosmos.key}")
    private String key;

    @Value("${spring.cloud.azure.cosmos.database}")
    private String database;

    @Bean
    public CosmosConfig cosmosConfig() {
        return CosmosConfig.builder()
                .enableQueryMetrics(true)
                .maxDegreeOfParallelism(1000)
                .maxBufferedItemCount(100)
                .build();
    }

    @Override
    protected String getDatabaseName() {
        return database;
    }

    // Override to provide the endpoint and key
    public String getUri() {
        return endpoint;
    }

    public String getKey() {
        return key;
    }
}