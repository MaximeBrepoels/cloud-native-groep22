package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Progress;
import cloudnative.fitapp.domain.Set;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.enums.WorkoutType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ExerciseService {

    private final CosmosDBService cosmosDBService;
    private final WorkoutService workoutService;

    public ExerciseService(CosmosDBService cosmosDBService, WorkoutService workoutService) {
        this.cosmosDBService = cosmosDBService;
        this.workoutService = workoutService;
    }

    public List<Exercise> getAllExercises() {
        List<Workout> workouts = cosmosDBService.findAll("workouts", Workout.class);
        List<Exercise> allExercises = new ArrayList<>();
        
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                for (Exercise exercise : workout.getExercises()) {
                    exercise.setWorkout(workout);
                    allExercises.add(exercise);
                }
            }
        }
        return allExercises;
    }

    public Exercise getExerciseById(Long id) {
        String exerciseId = String.valueOf(id);
        List<Workout> workouts = cosmosDBService.findAll("workouts", Workout.class);
        
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                for (Exercise exercise : workout.getExercises()) {
                    if (exerciseId.equals(exercise.getId()) || 
                        String.valueOf(exercise.getId()).equals(exerciseId)) {
                        exercise.setWorkout(workout);
                        return exercise;
                    }
                }
            }
        }
        throw new RuntimeException("Exercise not found with id: " + id);
    }

    public List<Exercise> getExercisesByWorkoutId(Long workoutId) {
        String query = String.format("SELECT * FROM c WHERE c.id = '%s'", workoutId);
        List<Workout> workouts = cosmosDBService.query("workouts", query, Workout.class);
        
        if (workouts.isEmpty()) {
            throw new RuntimeException("Workout not found");
        }
        
        return workouts.get(0).getExercises();
    }

    public Exercise createExercise(Exercise exercise, Long workoutId) {
        String query = String.format("SELECT * FROM c WHERE c.id = '%s'", workoutId);
        List<Workout> workouts = cosmosDBService.query("workouts", query, Workout.class);
        
        if (workouts.isEmpty()) {
            throw new RuntimeException("Workout not found with id: " + workoutId);
        }
        
        Workout workout = workouts.get(0);
        exercise.setWorkout(workout);
        exercise.setId(String.valueOf(System.currentTimeMillis() + (int)(Math.random() * 1000)));

        if (workout.getExercises() == null) {
            workout.setExercises(new ArrayList<>());
        }
        workout.getExercises().add(exercise);

        cosmosDBService.update("workouts", workout, workout.getUserId(), Workout.class);
        return exercise;
    }

    public Exercise createExerciseByName(String exerciseName) {
        Exercise exercise = new Exercise(exerciseName);
        exercise.setId(String.valueOf(System.currentTimeMillis() + (int)(Math.random() * 1000)));
        return exercise;
    }

    public String deleteExerciseFromWorkout(Long workoutId, Long exerciseId) {
        String query = String.format("SELECT * FROM c WHERE c.id = '%s'", workoutId);
        List<Workout> workouts = cosmosDBService.query("workouts", query, Workout.class);
        
        if (workouts.isEmpty()) {
            throw new RuntimeException("Workout not found with id: " + workoutId);
        }
        
        Workout workout = workouts.get(0);
        String exerciseIdStr = String.valueOf(exerciseId);
        boolean removed = workout.getExercises().removeIf(e ->
                exerciseIdStr.equals(e.getId()) || String.valueOf(e.getId()).equals(exerciseIdStr));

        if (!removed) {
            throw new RuntimeException("Exercise not found in workout");
        }

        cosmosDBService.update("workouts", workout, workout.getUserId(), Workout.class);
        return "Exercise successfully deleted from workout";
    }

    public Exercise updateExercise(Long id, Exercise newValuesExercise) {
        Exercise exercise = getExerciseById(id);
        Workout workout = exercise.getWorkout();

        if (exercise.getType() != newValuesExercise.getType()) {
            exercise.clearProgress();
        }

        // Update all exercise properties
        exercise.setName(newValuesExercise.getName());
        exercise.setType(newValuesExercise.getType());
        exercise.setRest(newValuesExercise.getRest());
        exercise.setAutoIncrease(newValuesExercise.getAutoIncrease());
        exercise.setAutoIncreaseFactor(newValuesExercise.getAutoIncreaseFactor());
        exercise.setAutoIncreaseWeightStep(newValuesExercise.getAutoIncreaseWeightStep());
        exercise.setAutoIncreaseStartWeight(newValuesExercise.getAutoIncreaseStartWeight());
        exercise.setAutoIncreaseMinSets(newValuesExercise.getAutoIncreaseMinSets());
        exercise.setAutoIncreaseMaxSets(newValuesExercise.getAutoIncreaseMaxSets());
        exercise.setAutoIncreaseMinReps(newValuesExercise.getAutoIncreaseMinReps());
        exercise.setAutoIncreaseMaxReps(newValuesExercise.getAutoIncreaseMaxReps());
        exercise.setAutoIncreaseStartDuration(newValuesExercise.getAutoIncreaseStartDuration());
        exercise.setAutoIncreaseDurationSets(newValuesExercise.getAutoIncreaseDurationSets());
        exercise.setAutoIncreaseCurrentSets(newValuesExercise.getAutoIncreaseCurrentSets());
        exercise.setAutoIncreaseCurrentReps(newValuesExercise.getAutoIncreaseCurrentReps());
        exercise.setAutoIncreaseCurrentWeight(newValuesExercise.getAutoIncreaseCurrentWeight());
        exercise.setAutoIncreaseCurrentDuration(newValuesExercise.getAutoIncreaseCurrentDuration());

        if (newValuesExercise.getSets() != null) {
            exercise.setSets(new ArrayList<>());

            for (Set set : newValuesExercise.getSets()) {
                Set newSet = new Set(set.getReps(), set.getWeight(), set.getDuration());
                newSet.setId(set.getId());
                newSet.setExercise(exercise);
                exercise.getSets().add(newSet);
            }
        }

        cosmosDBService.update("workouts", workout, workout.getUserId(), Workout.class);
        return exercise;
    }

    public Exercise autoIncrease(Long id) {
        Exercise exercise = getExerciseById(id);

        if (exercise.getType() == WorkoutType.WEIGHTS) {
            double currentWeight = exercise.getAutoIncreaseCurrentWeight();
            double newWeight = currentWeight + exercise.getAutoIncreaseWeightStep();
            exercise.setAutoIncreaseCurrentWeight(newWeight);

            Progress progress = new Progress(newWeight, new Date());
            exercise.addProgress(progress);
        } else if (exercise.getType() == WorkoutType.DURATION) {
            int currentDuration = exercise.getAutoIncreaseCurrentDuration();
            int newDuration = (int)(currentDuration * exercise.getAutoIncreaseFactor());
            exercise.setAutoIncreaseCurrentDuration(newDuration);

            Progress progress = new Progress(newDuration, new Date());
            exercise.addProgress(progress);
        }

        cosmosDBService.update("workouts", exercise.getWorkout(), exercise.getWorkout().getUserId(), Workout.class);
        return exercise;
    }

    public Exercise autoDecrease(Long id) {
        Exercise exercise = getExerciseById(id);

        if (exercise.getType() == WorkoutType.WEIGHTS) {
            double currentWeight = exercise.getAutoIncreaseCurrentWeight();
            double newWeight = Math.max(exercise.getAutoIncreaseStartWeight(),
                    currentWeight - exercise.getAutoIncreaseWeightStep());
            exercise.setAutoIncreaseCurrentWeight(newWeight);
        } else if (exercise.getType() == WorkoutType.DURATION) {
            int currentDuration = exercise.getAutoIncreaseCurrentDuration();
            int newDuration = Math.max(exercise.getAutoIncreaseStartDuration(),
                    (int)(currentDuration / exercise.getAutoIncreaseFactor()));
            exercise.setAutoIncreaseCurrentDuration(newDuration);
        }

        cosmosDBService.update("workouts", exercise.getWorkout(), exercise.getWorkout().getUserId(), Workout.class);
        return exercise;
    }

    public List<Exercise> getExercisesByUserId(Long userId) {
        String query = String.format("SELECT * FROM c WHERE c.userId = '%s'", userId);
        List<Workout> workouts = cosmosDBService.query("workouts", query, Workout.class);
        
        List<Exercise> exercises = new ArrayList<>();
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
