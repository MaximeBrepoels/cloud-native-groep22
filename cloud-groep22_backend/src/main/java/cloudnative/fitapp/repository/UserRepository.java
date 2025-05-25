package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.User;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CosmosRepository<User, String> {

    @Query("SELECT * FROM c WHERE c.email = @email")
    User findByEmail(String email);

    Optional<User> findById(String id);

    default Optional<User> findById(Long id) {
        return findById(String.valueOf(id));
    }

    default boolean existsById(Long id) {
        return existsById(String.valueOf(id));
    }

    default void deleteById(Long id) {
        deleteById(String.valueOf(id));
    }
}