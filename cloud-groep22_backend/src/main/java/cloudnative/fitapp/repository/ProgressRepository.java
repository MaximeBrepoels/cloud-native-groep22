package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Progress;
import cloudnative.fitapp.domain.Workout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProgressRepository {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<Progress> findAll() {
        List<Progress> allProgress = new ArrayList<>();
        List<Workout> workouts = (List<Workout>) workoutRepository.findAll();
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

    public List<Progress> findByExerciseId(Long id) {
        Optional<Exercise> exerciseOpt = exerciseRepository.findById(id);
        if (exerciseOpt.isPresent() && exerciseOpt.get().getProgressList() != null) {
            return exerciseOpt.get().getProgressList();
        }
        return new ArrayList<>();
    }
}