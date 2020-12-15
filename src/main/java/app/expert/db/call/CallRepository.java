package app.expert.db.call;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {

    Optional<Call> findByIdentifier(String identifier);

    List<Call> findByManager(Long id);
}
