package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class WorkoutService {

    private final CosmosDBService cosmosDBService;
    private final UserService userService;

    public WorkoutService(CosmosDBService cosmosDBService, UserService userService) {
        this.cosmosDBService = cosmosDBService;
        this.userService = userService;
    }

    public Workout createWorkout(String workoutName, String userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                throw new RuntimeException("User not found with id: " + userId);
            }

            Workout workout = new Workout(workoutName);
            workout.setId(String.valueOf(System.currentTimeMillis()));
            workout.setUserId(userId);

            // Update user's workout IDs
            if (user.getWorkoutIds() == null) {
                user.setWorkoutIds(new ArrayList<>());
            }
            user.getWorkoutIds().add(workout.getId());
            userService.updateUser(user.getEmail(), user);

            return cosmosDBService.save("workouts", workout, userId, Workout.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<Workout> getAllWorkouts() {
        return cosmosDBService.findAll("workouts", Workout.class);
    }

    public Optional<Workout> getWorkoutById(String id) {
        String query = String.format("SELECT * FROM c WHERE c.id = '%s'", id);
        List<Workout> workouts = cosmosDBService.query("workouts", query, Workout.class);
        return workouts.isEmpty() ? Optional.empty() : Optional.of(workouts.get(0));
    }

    public void deleteWorkout(String id) {
        Optional<Workout> workoutOpt = getWorkoutById(id);
        if (workoutOpt.isPresent()) {
            Workout workout = workoutOpt.get();

            // Remove workout ID from user
            User user = userService.getUserById(workout.getUserId());
            if (user != null && user.getWorkoutIds() != null) {
                user.getWorkoutIds().remove(workout.getId());
                userService.updateUser(user.getEmail(), user);
            }

            cosmosDBService.deleteById("workouts", id, workout.getUserId());
        }
    }

    public Workout updateWorkout(String id, String workoutName, Integer rest, List<String> exerciseIds) {
        Optional<Workout> optionalWorkout = getWorkoutById(id);
        if (optionalWorkout.isEmpty()) {
            throw new RuntimeException("Workout not found with id: " + id);
        }

        Workout workout = optionalWorkout.get();
        workout.setName(workoutName);
        workout.setRest(rest);

        return cosmosDBService.update("workouts", workout, workout.getUserId(), Workout.class);
    }

    public Exercise addExerciseToWorkout(String workoutId, Exercise exercise, String goal) {
        Workout workout = getWorkoutById(workoutId).orElseThrow(() ->
                new RuntimeException("Workout not found with id: " + workoutId));

        Exercise newExercise = new Exercise(exercise.getName(), exercise.getType(), goal);
        newExercise = workout.addExercise(newExercise);

        cosmosDBService.update("workouts", workout, workout.getUserId(), Workout.class);
        return newExercise;
    }

    public List<Workout> getWorkoutsByUserId(String userId) {
        String query = String.format("SELECT * FROM c WHERE c.userId = '%s'", userId);
        return cosmosDBService.query("workouts", query, Workout.class);
    }
}