package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Set;
import cloudnative.fitapp.domain.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java Set Service for Azure Functions.
 */
public class SetService {

    private final CosmosDBService cosmosDBService;
    private final ExerciseService exerciseService;

    public SetService(CosmosDBService cosmosDBService, ExerciseService exerciseService) {
        this.cosmosDBService = cosmosDBService;
        this.exerciseService = exerciseService;
    }

    public List<Set> getAllSets() {
        List<Set> allSets = new ArrayList<>();
        List<Workout> workouts = cosmosDBService.findAll("workouts", Workout.class);
        
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                for (Exercise exercise : workout.getExercises()) {
                    if (exercise.getSets() != null) {
                        allSets.addAll(exercise.getSets());
                    }
                }
            }
        }
        return allSets;
    }

    public Set getSetById(Long id) {
        String setId = String.valueOf(id);
        List<Workout> workouts = cosmosDBService.findAll("workouts", Workout.class);
        
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                for (Exercise exercise : workout.getExercises()) {
                    if (exercise.getSets() != null) {
                        for (Set set : exercise.getSets()) {
                            if (setId.equals(set.getId()) || 
                                String.valueOf(set.getId()).equals(setId)) {
                                set.setExercise(exercise);
                                return set;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public Set addSetToExercise(Long exerciseId, Set set) {
        Exercise exercise = exerciseService.getExerciseById(exerciseId);
        Set newSet = exercise.addSet(set);
        cosmosDBService.update("workouts", exercise.getWorkout(), exercise.getWorkout().getUserId(), Workout.class);
        return newSet;
    }

    public void deleteSet(Long id) {
        String setId = String.valueOf(id);
        List<Workout> workouts = cosmosDBService.findAll("workouts", Workout.class);
        
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                for (Exercise exercise : workout.getExercises()) {
                    if (exercise.getSets() != null) {
                        exercise.getSets().removeIf(s ->
                                setId.equals(s.getId()) ||
                                        String.valueOf(s.getId()).equals(setId)
                        );
                    }
                }
            }
            cosmosDBService.update("workouts", workout, workout.getUserId(), Workout.class);
        }
    }

    public Set updateSet(Long id, Set newValuesSet) {
        Set set = getSetById(id);
        if (set != null) {
            set.updateValuesSet(newValuesSet.getReps(), newValuesSet.getWeight(),
                    newValuesSet.getDuration(), newValuesSet.getExercise());
            Exercise exercise = set.getExercise();
            cosmosDBService.update("workouts", exercise.getWorkout(), exercise.getWorkout().getUserId(), Workout.class);
        }
        return set;
    }
}
