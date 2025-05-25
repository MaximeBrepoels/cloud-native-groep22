package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.Exercise;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Query("SELECT e FROM Exercise e JOIN e.workout w WHERE w.user.id = :userId")
    
    List<Exercise> findByUserId(@Param("userId") Long userId);

}
