package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Progress;
import cloudnative.fitapp.domain.Set;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.enums.WorkoutType;
import cloudnative.fitapp.exception.ExerciseServiceException;
import cloudnative.fitapp.repository.ExerciseRepository;
import cloudnative.fitapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private WorkoutRepository workoutRepository;

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Exercise getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id).orElse(null);
        if (exercise != null) {
            return exercise;
        } else {
            throw new ExerciseServiceException("Exercise not found with id: " + id);
        }
    }

    public List<Exercise> getExercisesByWorkoutId(Long workoutId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));
        return workout.getExercises();
    }

    public Exercise createExercise(Exercise exercise, Long workoutId) {
        Workout workout = workoutService.getWorkoutById(workoutId).orElse(null);
        if (workout != null) {
            exercise.setWorkout(workout);
            exercise.setId(String.valueOf(System.currentTimeMillis()));
            return exerciseRepository.save(exercise);
        } else {
            throw new ExerciseServiceException("Workout not found with id: " + workoutId);
        }
    }

    public Exercise createExerciseByName(String exerciseName) {
        // This method doesn't make sense without a workout context in Cosmos DB
        // since exercises are embedded in workouts
        throw new ExerciseServiceException("Exercise must be created within a workout");
    }

    public String deleteExerciseFromWorkout(Long workoutId, Long exerciseId) {
        Workout workout = workoutService.getWorkoutById(workoutId)
                .orElseThrow(() -> new ExerciseServiceException("Workout not found with id: " + workoutId));

        Exercise exercise = getExerciseById(exerciseId);
        if (!exercise.getWorkout().getId().equals(workoutId)) {
            throw new ExerciseServiceException("Exercise does not belong to the specified workout");
        }

        workout.getExercises().removeIf(e -> e.getId().equals(exercise.getId()));
        workoutRepository.save(workout);
        return "Exercise successfully deleted from workout";
    }

    public Exercise updateExercise(Long id, Exercise newValuesExercise) {
        Exercise exercise = getExerciseById(id);
        Workout workout = exercise.getWorkout();

        if (exercise.getType() != newValuesExercise.getType()) {
            exercise.clearProgress();
        }

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

        List<Set> existingSets = exercise.getSets();
        List<Set> newSets = newValuesExercise.getSets();

        for (Set newSet : newSets) {
            boolean updated = false;
            for (Set existingSet : existingSets) {
                if (existingSet.getId().equals(newSet.getId())) {
                    existingSet.updateValuesSet(newSet.getReps(), newSet.getWeight(), newSet.getDuration(), exercise);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                newSet.setExercise(exercise);
                existingSets.add(newSet);
            }
        }

        if (exercise.getAutoIncrease() && exercise.getProgressList().size() <= 0) {
            if (exercise.getAutoIncrease() && exercise.getType().equals(WorkoutType.WEIGHTS)) {
                addProgressWeight(exercise.getId(), exercise.getAutoIncreaseStartWeight(), new Date());
            } else if (exercise.getAutoIncrease() && exercise.getType().equals(WorkoutType.DURATION)) {
                addProgressDuration(exercise.getId(), exercise.getAutoIncreaseStartDuration(), new Date());
            }
        }

        existingSets.removeIf(
                existingSet -> newSets.stream().noneMatch(newSet -> newSet.getId().equals(existingSet.getId())));

        // Save the parent workout to persist changes
        workoutRepository.save(workout);
        return exercise;
    }

    public Exercise autoIncrease(Long id) {
        Exercise exercise = getExerciseById(id);
        if (exercise.getAutoIncrease()) {
            int currentSets = exercise.getAutoIncreaseCurrentSets();
            int currentReps = exercise.getAutoIncreaseCurrentReps();
            int currentDuration = exercise.getAutoIncreaseCurrentDuration();
            int minSets = exercise.getAutoIncreaseMinSets();
            int maxSets = exercise.getAutoIncreaseMaxSets();
            int minReps = exercise.getAutoIncreaseMinReps();
            int maxReps = exercise.getAutoIncreaseMaxReps();
            double currentWeight = exercise.getAutoIncreaseCurrentWeight();
            double weightStep = exercise.getAutoIncreaseWeightStep();
            double factor = exercise.getAutoIncreaseFactor();

            if (exercise.getType().equals(WorkoutType.DURATION)) {
                currentDuration = addDuration(currentDuration, factor);
                addProgressDuration(id, currentDuration, new Date());
            } else {
                addProgressWeight(id, currentWeight, new Date());
                currentReps = addRepsAndSets(currentReps, factor);

                if (currentReps >= maxReps) {
                    currentReps = minReps;
                    currentSets = addRepsAndSets(currentSets, factor);

                    if (exercise.getType().equals(WorkoutType.WEIGHTS)) {
                        if (currentSets >= maxSets) {
                            currentSets = minSets;
                            currentWeight = addWeight(currentWeight, factor, weightStep);
                        }
                    }
                    if (exercise.getType().equals(WorkoutType.BODYWEIGHT)) {
                        if (currentSets >= maxSets) {
                            currentSets = maxSets;
                        }
                    }
                }
            }
            exercise.setAutoIncreaseCurrentSets(currentSets);
            exercise.setAutoIncreaseCurrentReps(currentReps);
            exercise.setAutoIncreaseCurrentDuration(currentDuration);
            exercise.setAutoIncreaseCurrentWeight(currentWeight);
            exercise.setAutoIncreaseWeightStep(weightStep);
            exercise.setAutoIncreaseFactor(factor);
        }

        // Save the parent workout
        workoutRepository.save(exercise.getWorkout());
        return exercise;
    }

    public Exercise autoDecrease(Long id) {
        Exercise exercise = getExerciseById(id);
        if (exercise.getAutoIncrease()) {
            int currentSets = exercise.getAutoIncreaseCurrentSets();
            int currentReps = exercise.getAutoIncreaseCurrentReps();
            int currentDuration = exercise.getAutoIncreaseCurrentDuration();
            int minSets = exercise.getAutoIncreaseMinSets();
            int maxSets = exercise.getAutoIncreaseMaxSets();
            int minReps = exercise.getAutoIncreaseMinReps();
            int maxReps = exercise.getAutoIncreaseMaxReps();
            double currentWeight = exercise.getAutoIncreaseCurrentWeight();
            double weightStep = exercise.getAutoIncreaseWeightStep();
            double factor = exercise.getAutoIncreaseFactor();

            if (exercise.getType().equals(WorkoutType.DURATION)) {
                currentDuration = subtractDuration(currentDuration, factor);
                addProgressDuration(id, currentDuration, new Date());
            } else {
                currentReps = subtractRepsAndSets(currentReps, factor);

                if (currentReps <= minReps) {
                    currentReps = maxReps;
                    currentSets = subtractRepsAndSets(currentSets, factor);

                    if (exercise.getType().equals(WorkoutType.WEIGHTS)) {
                        if (currentSets <= minSets) {
                            currentSets = maxSets;
                            currentWeight = subtractWeight(currentWeight, factor, weightStep);
                            addProgressWeight(id, currentWeight, new Date());
                        }
                    }
                    if (exercise.getType().equals(WorkoutType.BODYWEIGHT)) {
                        if (currentSets <= minSets) {
                            currentSets = minSets;
                        }
                    }
                }
            }
            exercise.setAutoIncreaseCurrentSets(currentSets);
            exercise.setAutoIncreaseCurrentReps(currentReps);
            exercise.setAutoIncreaseCurrentDuration(currentDuration);
            exercise.setAutoIncreaseCurrentWeight(currentWeight);
            exercise.setAutoIncreaseWeightStep(weightStep);
            exercise.setAutoIncreaseFactor(factor);
        }

        // Save the parent workout
        workoutRepository.save(exercise.getWorkout());
        return exercise;
    }

    public int addRepsAndSets(int value, double multiplier) {
        return Math.max(value + 1, (int) Math.round(value * multiplier));
    }

    public int addDuration(int value, double multiplier) {
        System.out.println("add duration " + Math.max(value + 5, (int) Math.round(value * multiplier)));
        return Math.max(value + 5, (int) Math.round(value * multiplier));
    }

    public double addWeight(double value, double multiplier, double weightStep) {
        double newWeight = value * multiplier;
        return Math.ceil(newWeight / weightStep) * weightStep;
    }

    public int subtractRepsAndSets(int value, double multiplier) {
        return Math.max(value - 1, (int) Math.round(value / multiplier));
    }

    public int subtractDuration(int value, double multiplier) {
        return Math.max(value - 5, (int) Math.round(value / multiplier));
    }

    public double subtractWeight(double value, double multiplier, double weightStep) {
        double newWeight = value / multiplier;
        return Math.floor(newWeight / weightStep) * weightStep;
    }

    public Exercise addProgressWeight(Long exerciseId, double weight, Date date) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseServiceException("Exercise not found"));

        if (exercise.getProgressList() == null) {
            exercise.setProgressList(new ArrayList<>());
        }

        if (exercise.getProgressList().size() > 1 &&
                secondProgress(exercise.getProgressList()) &&
                exercise.getProgressList().get(exercise.getProgressList().size() - 1).getWeight().equals(weight)) {
            exercise.getProgressList().get(exercise.getProgressList().size() - 1).setDate(date);
        } else {
            Progress progress = new Progress(weight, date);
            progress.setExercise(exercise);
            exercise.addProgress(progress);
        }

        // Save the parent workout
        workoutRepository.save(exercise.getWorkout());
        return exercise;
    }

    private boolean secondProgress(List<Progress> progressList) {
        int length = progressList.size();
        if (progressList.get(length - 1).getWeight().equals(progressList.get(length - 2).getWeight())) {
            return true;
        }
        return false;
    }

    public Progress addProgressDuration(Long exerciseId, int duration, Date date) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseServiceException("Exercise not found"));

        if (exercise.getProgressList() == null) {
            exercise.setProgressList(new ArrayList<>());
        }

        Progress progress = new Progress(duration, date);
        progress.setExercise(exercise);
        exercise.addProgress(progress);

        // Save the parent workout
        workoutRepository.save(exercise.getWorkout());
        return progress;
    }

    public List<Exercise> getExercisesByUserId(Long userId) {
        List<Exercise> exercises = exerciseRepository.findByUserId(userId);

        if (exercises != null && !exercises.isEmpty()) {
            System.out.println("exercises: " + exercises.get(0).getProgressList());
            return exercises;
        } else {
            return new ArrayList<Exercise>();
        }
    }
}