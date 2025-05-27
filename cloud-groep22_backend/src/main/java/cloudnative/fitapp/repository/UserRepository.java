package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.User;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CosmosRepository<User, String> {

    // Use Spring Data method naming - this should work better with Cosmos DB
    List<User> findByEmail(String email);

    // Add a convenience method for getting a single user by email
    default User findSingleByEmail(String email) {
        List<User> users = findByEmail(email);
        return users.isEmpty() ? null : users.get(0);
    }

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