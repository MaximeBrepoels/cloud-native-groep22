package cloudnative.fitapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.repository.UserRepository;

import java.util.List;

@Configuration
@Profile("test-connection")
public class CosmosConnectionTest {

    @Bean
    CommandLineRunner testConnection(UserRepository userRepository) {
        return args -> {
            System.out.println("🔄 Testing Cosmos DB connection...");

            try {
                // Test basic connection
                long userCount = userRepository.count();
                System.out.println("✅ Connection successful! User count: " + userCount);

                // Test create operation
                String testEmail = "test@connection.com";

                // Clean up any existing test user
                List<User> existingUsers = userRepository.findByEmail(testEmail);
                if (!existingUsers.isEmpty()) {
                    userRepository.delete(existingUsers.get(0));
                    System.out.println("🧹 Cleaned up existing test user");
                }

                // Create new test user
                User testUser = new User("Connection Test", testEmail, "testpassword123");
                testUser.setId("connection-test-" + System.currentTimeMillis());
                User savedUser = userRepository.save(testUser);
                System.out.println("✅ Successfully created test user with ID: " + savedUser.getId());

                // Test read operation
                List<User> retrievedUsers = userRepository.findByEmail(testEmail);
                if (!retrievedUsers.isEmpty()) {
                    User retrieved = retrievedUsers.get(0);
                    System.out.println("✅ Successfully retrieved test user: " + retrieved.getName());

                    // Clean up
                    userRepository.delete(retrieved);
                    System.out.println("🧹 Deleted test user");
                }

                System.out.println("\n🎉 All Cosmos DB operations working correctly!");

            } catch (Exception e) {
                System.err.println("❌ Connection failed: " + e.getMessage());
                System.err.println("💡 Please check:");
                System.err.println("   - AZURE_COSMOS_URI environment variable");
                System.err.println("   - AZURE_COSMOS_KEY environment variable");
                System.err.println("   - AZURE_COSMOS_DATABASE_NAME environment variable");
                System.err.println("   - Network connectivity to Azure");
                System.err.println("   - Firewall settings in Azure Cosmos DB");
                e.printStackTrace();
            }
        };
    }
}