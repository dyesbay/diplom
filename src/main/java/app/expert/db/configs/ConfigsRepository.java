package app.expert.db.configs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigsRepository extends JpaRepository<Config, String> {
}
