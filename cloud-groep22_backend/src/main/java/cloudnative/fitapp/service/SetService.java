package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Set;
import cloudnative.fitapp.repository.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetService {

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private ExerciseService exerciseService;

    public List<Set> getAllSets() {
        return setRepository.findAll();
    }

    public Set getSetById(Long id) {
        return setRepository.findById(id).orElse(null);
    }

    public Set addSetToExercise(Long exerciseId, Set set) {
        Exercise exercise = exerciseService.getExerciseById(exerciseId);

        Set newSet = exercise.addSet(set);

        return setRepository.save(newSet);
    }

    public void deleteSet(Long id) {
        setRepository.deleteById(id);
    }

    public Set updateSet(Long id, Set newValuesSet) {
        Set set = getSetById(id);

        set.updateValuesSet(newValuesSet.getReps(), newValuesSet.getWeight(), newValuesSet.getDuration(),
                newValuesSet.getExercise());

        return setRepository.save(set);
    }
}