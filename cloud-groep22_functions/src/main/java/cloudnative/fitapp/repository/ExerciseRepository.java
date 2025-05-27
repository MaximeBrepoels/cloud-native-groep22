package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Workout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ExerciseRepository {

    @Autowired
    private WorkoutRepository workoutRepository;

    public List<Exercise> findAll() {
        List<Exercise> allExercises = new ArrayList<>();
        List<Workout> workouts = (List<Workout>) workoutRepository.findAll();
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                allExercises.addAll(workout.getExercises());
            }
        }
        return allExercises;
    }

    public Optional<Exercise> findById(Long id) {
        String exerciseId = String.valueOf(id);
        List<Workout> workouts = (List<Workout>) workoutRepository.findAll();
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                for (Exercise exercise : workout.getExercises()) {
                    if (exerciseId.equals(exercise.getId()) ||
                            String.valueOf(exercise.getId()).equals(exerciseId)) {
                        exercise.setWorkout(workout);
                        return Optional.of(exercise);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public Exercise save(Exercise exercise) {
        if (exercise.getWorkout() != null) {
            Workout workout = exercise.getWorkout();

            // Update exercise in workout's exercise list
            boolean found = false;
            if (workout.getExercises() != null) {
                for (int i = 0; i < workout.getExercises().size(); i++) {
                    if (workout.getExercises().get(i).getId().equals(exercise.getId())) {
                        workout.getExercises().set(i, exercise);
                        found = true;
                        break;
                    }
                }
            }

            if (!found && workout.getExercises() != null) {
                workout.getExercises().add(exercise);
            }

            workoutRepository.save(workout);
        }
        return exercise;
    }

    public void deleteById(Long id) {
        String exerciseId = String.valueOf(id);
        List<Workout> workouts = (List<Workout>) workoutRepository.findAll();
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                workout.getExercises().removeIf(e ->
                        exerciseId.equals(e.getId()) ||
                                String.valueOf(e.getId()).equals(exerciseId)
                );
                workoutRepository.save(workout);
            }
        }
    }

    public List<Exercise> findByUserId(String userId) {
        List<Exercise> exercises = new ArrayList<>();
        List<Workout> workouts = workoutRepository.findWorkoutsByUserId(userId);
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                for (Exercise exercise : workout.getExercises()) {
                    exercise.setWorkout(workout);
                    exercises.add(exercise);
                }
            }
        }
        return exercises;
    }
}