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
    List<Workout> findWorkoutsByUserId(String userId); // Changed to String

    Optional<Workout> findById(String id);
}