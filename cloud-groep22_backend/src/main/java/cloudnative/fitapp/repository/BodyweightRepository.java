package cloudnative.fitapp.repository;

import cloudnative.fitapp.domain.Bodyweight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyweightRepository extends JpaRepository<Bodyweight, Long> {

    List<Bodyweight> findByUserId(Long id);
}
