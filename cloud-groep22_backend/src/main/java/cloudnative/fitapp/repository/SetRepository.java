package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetRepository extends JpaRepository<Set, Long> {
    
}
