package cloudnative.fitapp.repository;

import jakarta.annotation.PostConstruct;
import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Progress;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.enums.WorkoutType;
import cloudnative.fitapp.repository.UserRepository;
import cloudnative.fitapp.repository.WorkoutRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CosmosDbInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WorkoutRepository workoutRepository;

    @PostConstruct
    public void init() {
        try {
            // Check if data already exists
            long userCount = userRepository.count();
            if (userCount > 0) {
                System.out.println("Database already initialized with " + userCount + " users");
                return;
            }

            System.out.println("Initializing Cosmos DB with sample data...");

            // Create test users
            User user1 = new User("test", "test@test.com", passwordEncoder.encode("test1234"));
            user1.setId("1");
            userRepository.save(user1);

            User user2 = new User("admin", "admin@test.com", passwordEncoder.encode("admin1234"));
            user2.setId("2");
            userRepository.save(user2);

            // Create workout for user1
            Workout workoutUser1 = new Workout("Push");
            workoutUser1.setId("1");
            workoutUser1.setUser(user1);

            // Create exercises
            Exercise exercise1 = new Exercise("Bench Press", WorkoutType.WEIGHTS, "MUSCLE");
            exercise1.setId("1");

            Exercise exercise2 = new Exercise("Plank", WorkoutType.DURATION, "ENDURANCE");
            exercise2.setId("2");

            // Add progress to exercises
            Progress progress1 = new Progress(20.0, new Date());
            progress1.setId("p1");
            Progress progress2 = new Progress(30.0, new Date());
            progress2.setId("p2");
            Progress progress3 = new Progress(40.0, new Date());
            progress3.setId("p3");
            Progress progress4 = new Progress(50.0, new Date());
            progress4.setId("p4");

            exercise1.addProgress(progress1);
            exercise1.addProgress(progress2);
            exercise1.addProgress(progress3);
            exercise1.addProgress(progress4);

            Progress progress5 = new Progress(20, new Date());
            progress5.setId("p5");
            Progress progress6 = new Progress(30, new Date());
            progress6.setId("p6");
            Progress progress7 = new Progress(40, new Date());
            progress7.setId("p7");
            Progress progress8 = new Progress(50, new Date());
            progress8.setId("p8");

            exercise2.addProgress(progress5);
            exercise2.addProgress(progress6);
            exercise2.addProgress(progress7);
            exercise2.addProgress(progress8);

            // Add exercises to workout
            workoutUser1.addExercise(exercise1);
            workoutUser1.addExercise(exercise2);

            workoutRepository.save(workoutUser1);

            // Create workout for user2
            Workout workoutUser2 = new Workout("Pull");
            workoutUser2.setId("2");
            workoutUser2.setUser(user2);
            workoutRepository.save(workoutUser2);

            System.out.println("✅ Cosmos DB initialized successfully!");
            System.out.println("   - Created 2 users");
            System.out.println("   - Created 2 workouts");
            System.out.println("   - Created 2 exercises with progress data");

        } catch (Exception e) {
            System.err.println("❌ Failed to initialize Cosmos DB: " + e.getMessage());
            // Don't throw the exception to prevent app startup failure
            e.printStackTrace();
        }
    }
}
