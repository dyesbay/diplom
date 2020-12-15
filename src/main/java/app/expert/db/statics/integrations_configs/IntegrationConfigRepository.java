package app.expert.db.statics.integrations_configs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationConfigRepository extends JpaRepository<IntegrationConfig, String> {
}
