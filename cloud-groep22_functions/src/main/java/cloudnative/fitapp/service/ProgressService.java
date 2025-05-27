package cloudnative.fitapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cloudnative.fitapp.domain.Progress;
import cloudnative.fitapp.repository.ProgressRepository;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;

    public ProgressService(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public List<Progress> getAllProgress() {
        return progressRepository.findAll();
    }

    public List<Progress> getProgressByExerciseId(Long id) {
        return progressRepository.findByExerciseId(id);
    }

}
