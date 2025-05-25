package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.Workout;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends CosmosRepository<Workout, String> {

    @Query("SELECT * FROM c WHERE c.userId = @userId")
    List<Workout> findWorkoutsByUserId(Long userId);

    Optional<Workout> findById(String id);

    default Optional<Workout> findById(Long id) {
        return findById(String.valueOf(id));
    }

    default void deleteById(Long id) {
        deleteById(String.valueOf(id));
    }
}