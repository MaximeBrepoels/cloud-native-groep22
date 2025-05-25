package cloudnative.fitapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloudnative.fitapp.domain.Progress;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

    List<Progress> findByExerciseId(Long id);

}
