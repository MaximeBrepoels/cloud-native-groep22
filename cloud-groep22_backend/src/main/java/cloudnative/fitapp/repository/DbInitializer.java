package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Progress;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.enums.WorkoutType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DbInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WorkoutRepository workoutRepository;

    @PostConstruct
    public void init() {
        // Clean up existing data
        workoutRepository.deleteAll();
        userRepository.deleteAll();

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
        Progress progress2 = new Progress(30.0, new Date());
        Progress progress3 = new Progress(40.0, new Date());
        Progress progress4 = new Progress(50.0, new Date());

        exercise1.addProgress(progress1);
        exercise1.addProgress(progress2);
        exercise1.addProgress(progress3);
        exercise1.addProgress(progress4);

        Progress progress5 = new Progress(20, new Date());
        Progress progress6 = new Progress(30, new Date());
        Progress progress7 = new Progress(40, new Date());
        Progress progress8 = new Progress(50, new Date());

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

        // Update users with workout IDs
        user1.getWorkoutIds().add(workoutUser1.getId());
        userRepository.save(user1);

        user2.getWorkoutIds().add(workoutUser2.getId());
        userRepository.save(user2);
    }
}