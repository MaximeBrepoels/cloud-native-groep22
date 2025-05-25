package cloudnative.fitapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.repository.UserRepository;
import cloudnative.fitapp.repository.WorkoutRepository;

@Configuration
@Profile("test-connection")
public class CosmosConnectionTest {

    @Bean
    CommandLineRunner testConnection(UserRepository userRepository, WorkoutRepository workoutRepository) {
        return args -> {
            System.out.println("🔄 Testing Cosmos DB connection...");

            try {
                // Test 1: Count users
                long userCount = userRepository.count();
                System.out.println("✅ Connection successful! User count: " + userCount);

                // Test 2: Count workouts
                long workoutCount = workoutRepository.count();
                System.out.println("✅ Workout count: " + workoutCount);

                // Test 3: Create a test user
                String testEmail = "test@connection.com";
                User existingUser = userRepository.findByEmail(testEmail);
                if (existingUser != null) {
                    userRepository.delete(existingUser);
                    System.out.println("🧹 Cleaned up existing test user");
                }

                User testUser = new User("Connection Test", testEmail, "testpassword123");
                testUser.setId("connection-test-" + System.currentTimeMillis());
                User savedUser = userRepository.save(testUser);
                System.out.println("✅ Successfully created test user with ID: " + savedUser.getId());

                // Test 4: Read it back
                User retrieved = userRepository.findByEmail(testEmail);
                if (retrieved != null) {
                    System.out.println("✅ Successfully retrieved test user: " + retrieved.getName());

                    // Test 5: Create a test workout
                    Workout testWorkout = new Workout("Test Connection Workout");
                    testWorkout.setId("workout-test-" + System.currentTimeMillis());
                    testWorkout.setUser(retrieved);
                    Workout savedWorkout = workoutRepository.save(testWorkout);
                    System.out.println("✅ Successfully created test workout with ID: " + savedWorkout.getId());

                    // Test 6: Query workout
                    var userWorkouts = workoutRepository.findWorkoutsByUserId(retrieved.getId());
                    System.out.println("✅ Found " + userWorkouts.size() + " workouts for test user");

                    // Clean up
                    workoutRepository.delete(savedWorkout);
                    System.out.println("🧹 Deleted test workout");

                    userRepository.delete(retrieved);
                    System.out.println("🧹 Deleted test user");
                }

                System.out.println("\n🎉 All Cosmos DB operations working correctly!");
                System.out.println("📊 Database contains " + userCount + " users and " + workoutCount + " workouts");

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