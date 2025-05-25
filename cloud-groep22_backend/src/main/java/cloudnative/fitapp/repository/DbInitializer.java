package cloudnative.fitapp.repository;

import jakarta.annotation.PostConstruct;
import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Progress;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.enums.WorkoutType;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DbInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;

    public DbInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
            WorkoutRepository workoutRepository, ExerciseRepository exerciseRepository,
            ProgressRepository progressRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @PostConstruct
    public void init() {
        userRepository.deleteAll();
        workoutRepository.deleteAll();

        User user1 = new User("test", "test@test.com", passwordEncoder.encode("test1234"));
        userRepository.save(user1);

        User user2 = new User("admin", "admin@test.com", passwordEncoder.encode("admin1234"));
        userRepository.save(user2);

        Workout workoutUser1 = new Workout("Push");
        workoutUser1.setUser(user1);
        workoutRepository.save(workoutUser1);

        Exercise exercise1 = new Exercise("Bench Press", WorkoutType.WEIGHTS, 60, true, 2, 20, 3, 5, 8, 12, 30, 3, 3, 8,
                30, 24);
        exercise1.setWorkout(workoutUser1);

        Exercise exercise2 = new Exercise("Plank", WorkoutType.DURATION, 60, true, 2, 20, 3, 5, 8, 12, 30, 3, 3, 8,
                30, 24);
        exercise2.setWorkout(workoutUser1);

        Progress progress1 = new Progress(20.0, new java.util.Date());
        Progress progress2 = new Progress(30.0, new java.util.Date());
        Progress progress3 = new Progress(40.0, new java.util.Date());
        Progress progress4 = new Progress(50.0, new java.util.Date());

        progress1.setExercise(exercise1);
        progress2.setExercise(exercise1);
        progress3.setExercise(exercise1);
        progress4.setExercise(exercise1);

        exercise1.addProgress(progress1);
        exercise1.addProgress(progress2);
        exercise1.addProgress(progress3);
        exercise1.addProgress(progress4);
        exerciseRepository.save(exercise1);

        Progress progress5 = new Progress(20, new java.util.Date());
        Progress progress6 = new Progress(30, new java.util.Date());
        Progress progress7 = new Progress(40, new java.util.Date());
        Progress progress8 = new Progress(50, new java.util.Date());

        progress5.setExercise(exercise2);
        progress6.setExercise(exercise2);
        progress7.setExercise(exercise2);
        progress8.setExercise(exercise2);

        exercise2.addProgress(progress5);
        exercise2.addProgress(progress6);
        exercise2.addProgress(progress7);
        exercise2.addProgress(progress8);
        exerciseRepository.save(exercise2);

        workoutUser1.addExercise(exercise1);
        workoutUser1.addExercise(exercise2);
        workoutRepository.save(workoutUser1);

        Workout workoutUser2 = new Workout("Pull");
        workoutUser2.setUser(user2);
        workoutRepository.save(workoutUser2);
    }
}
