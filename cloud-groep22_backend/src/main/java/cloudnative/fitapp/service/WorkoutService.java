package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.exception.WorkoutServiceException;
import cloudnative.fitapp.repository.ExerciseRepository;
import cloudnative.fitapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserService userService;

    public Workout createWorkout(String workoutName, Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                throw new WorkoutServiceException("User not found with id: " + userId);
            }
            Workout workout = new Workout(workoutName);
            workout.setId(Long.valueOf(String.valueOf(System.currentTimeMillis())));
            workout.setUser(user);

            // Update user's workout IDs
            if (user.getWorkoutIds() == null) {
                user.setWorkoutIds(new ArrayList<>());
            }
            user.getWorkoutIds().add(workout.getId());
            userService.updateUser(user.getEmail(), user);

            return workoutRepository.save(workout);
        } catch (Exception e) {
            throw new WorkoutServiceException(e.getMessage());
        }
    }

    public List<Workout> getAllWorkouts() {
        return (List<Workout>) workoutRepository.findAll();
    }

    public Optional<Workout> getWorkoutById(Long id) {
        return workoutRepository.findById(id);
    }

    public void deleteWorkout(Long id) {
        Optional<Workout> workoutOpt = workoutRepository.findById(id);
        if (workoutOpt.isPresent()) {
            Workout workout = workoutOpt.get();
            // Remove workout ID from user
            User user = userService.getUserById(Long.parseLong(workout.getUserId()));
            if (user != null && user.getWorkoutIds() != null) {
                user.getWorkoutIds().remove(workout.getId());
                userService.updateUser(user.getEmail(), user);
            }
        }
        workoutRepository.deleteById(id);
    }

    public Workout updateWorkout(Long id, String workoutName, Integer rest, List<Long> exerciseIds) {
        Optional<Workout> optionalWorkout = getWorkoutById(id);
        if (optionalWorkout.isEmpty()) {
            throw new WorkoutServiceException("Workout not found with id: " + id);
        }
        Workout workout = optionalWorkout.get();

        workout.setName(workoutName);
        workout.setRest(rest);

        List<Exercise> exercises = new ArrayList<>();
        for (Long exerciseId : exerciseIds) {
            Optional<Exercise> optionalExercise = exerciseRepository.findById(exerciseId);
            if (optionalExercise.isEmpty()) {
                throw new WorkoutServiceException("Exercise not found with id: " + exerciseId);
            }
            Exercise exercise = optionalExercise.get();
            exercises.add(exercise);
        }

        workout.updateExercisesOrder(exercises);

        return workoutRepository.save(workout);
    }

    public Exercise addExerciseToWorkout(Long workoutId, Exercise exercise, String goal) {
        Workout workout = getWorkoutById(workoutId).orElseThrow(() ->
                new WorkoutServiceException("Workout not found with id: " + workoutId));
        Exercise newExercise = new Exercise(exercise.getName(), exercise.getType(), goal);
        newExercise = workout.addExercise(newExercise);
        workoutRepository.save(workout);
        return newExercise;
    }

    public List<Workout> getWorkoutsByUserId(Long userId) {
        return workoutRepository.findWorkoutsByUserId(userId);
    }
}