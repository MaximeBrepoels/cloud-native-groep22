package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Progress;
import cloudnative.fitapp.domain.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java Progress Service for Azure Functions.
 */
public class ProgressService {

    private final CosmosDBService cosmosDBService;

    public ProgressService(CosmosDBService cosmosDBService) {
        this.cosmosDBService = cosmosDBService;
    }

    public List<Progress> getAllProgress() {
        List<Progress> allProgress = new ArrayList<>();
        List<Workout> workouts = cosmosDBService.findAll("workouts", Workout.class);
        
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                for (Exercise exercise : workout.getExercises()) {
                    if (exercise.getProgressList() != null) {
                        allProgress.addAll(exercise.getProgressList());
                    }
                }
            }
        }
        return allProgress;
    }

    public List<Progress> getProgressByExerciseId(Long id) {
        String exerciseId = String.valueOf(id);
        List<Workout> workouts = cosmosDBService.findAll("workouts", Workout.class);
        
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                for (Exercise exercise : workout.getExercises()) {
                    if (exerciseId.equals(exercise.getId()) || 
                        String.valueOf(exercise.getId()).equals(exerciseId)) {
                        return exercise.getProgressList() != null ? 
                               exercise.getProgressList() : new ArrayList<>();
                    }
                }
            }
        }
        return new ArrayList<>();
    }
}
