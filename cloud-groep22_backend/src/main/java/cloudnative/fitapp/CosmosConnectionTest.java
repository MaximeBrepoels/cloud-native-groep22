package cloudnative.fitapp;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.repository.UserRepository;

@Configuration
@Profile("test-connection")
public class CosmosConnectionTest {

    @Bean
    CommandLineRunner testConnection(UserRepository userRepository) {
        return args -> {
            System.out.println("Testing Cosmos DB connection...");

            try {
                // Try to count users
                long count = userRepository.count();
                System.out.println("✅ Connection successful! User count: " + count);

                // Try to create a test user
                User testUser = new User("Test", "test@connection.com", "test123");
                testUser.setId("connection-test-" + System.currentTimeMillis());
                userRepository.save(testUser);
                System.out.println("✅ Successfully created test user");

                // Try to read it back
                User retrieved = userRepository.findByEmail("test@connection.com");
                if (retrieved != null) {
                    System.out.println("✅ Successfully retrieved test user: " + retrieved.getName());

                    // Clean up
                    userRepository.delete(retrieved);
                    System.out.println("✅ Successfully deleted test user");
                }

                System.out.println("\n🎉 All Cosmos DB operations working correctly!");

            } catch (Exception e) {
                System.err.println("❌ Connection failed: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
