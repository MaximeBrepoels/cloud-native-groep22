package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.exception.WorkoutServiceException;
import cloudnative.fitapp.repository.ExerciseRepository;
import cloudnative.fitapp.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserService userService;

    public WorkoutService(WorkoutRepository workoutRepository, UserService userService, ExerciseRepository exerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.userService = userService;
    }

    public Workout createWorkout(String workoutName, Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                throw new WorkoutServiceException("User not found with id: " + userId);
            }
            Workout workout = new Workout(workoutName);
            workout.setUser(user);
            return workoutRepository.save(workout);
        } catch (Exception e) {
            throw new WorkoutServiceException(e.getMessage());
        }
    }

    public List<Workout> getAllWorkouts() {
        return workoutRepository.findAll();
    }

    public Optional<Workout> getWorkoutById(Long id) {
        return workoutRepository.findById(id);
    }

    public void deleteWorkout(Long id) {
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
        Workout workout = getWorkoutById(workoutId).orElseThrow(() -> new WorkoutServiceException("Workout not found with id: " + workoutId));
        Exercise newExercise = new Exercise(exercise.getName(), exercise.getType(), goal);
        newExercise = workout.addExercise(newExercise);
        return exerciseRepository.save(newExercise);
    }

    public List<Workout> getWorkoutsByUserId(Long userId) {
        return workoutRepository.findWorkoutsByUserId(userId);
    }
}
