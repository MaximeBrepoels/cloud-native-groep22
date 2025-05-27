package cloudnative.fitapp;

import cloudnative.fitapp.repository.UserRepository;
import cloudnative.fitapp.repository.WorkoutRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cleanup")
public class DataCleanup {

    @Bean
    CommandLineRunner cleanupData(UserRepository userRepository, WorkoutRepository workoutRepository) {
        return args -> {
            System.out.println("🧹 Cleaning up corrupted data...");

            try {
                // Delete all users and workouts to start fresh
                userRepository.deleteAll();
                workoutRepository.deleteAll();

                System.out.println("✅ All data cleaned up successfully!");
                System.out.println("You can now run the application normally.");

            } catch (Exception e) {
                System.err.println("❌ Cleanup failed: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}