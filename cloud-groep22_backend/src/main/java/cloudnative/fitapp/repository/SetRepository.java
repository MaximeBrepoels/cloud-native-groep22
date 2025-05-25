package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Set;
import cloudnative.fitapp.domain.Workout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SetRepository {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<Set> findAll() {
        List<Set> allSets = new ArrayList<>();
        List<Workout> workouts = (List<Workout>) workoutRepository.findAll();
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

    public Optional<Set> findById(Long id) {
        String setId = String.valueOf(id);
        List<Workout> workouts = (List<Workout>) workoutRepository.findAll();
        for (Workout workout : workouts) {
            if (workout.getExercises() != null) {
                for (Exercise exercise : workout.getExercises()) {
                    if (exercise.getSets() != null) {
                        for (Set set : exercise.getSets()) {
                            if (setId.equals(set.getId()) ||
                                    String.valueOf(set.getId()).equals(setId)) {
                                set.setExercise(exercise);
                                return Optional.of(set);
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    public Set save(Set set) {
        if (set.getExercise() != null) {
            exerciseRepository.save(set.getExercise());
        }
        return set;
    }

    public void deleteById(Long id) {
        String setId = String.valueOf(id);
        List<Workout> workouts = (List<Workout>) workoutRepository.findAll();
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
            workoutRepository.save(workout);
        }
    }
}