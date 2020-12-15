package app.expert.db.manager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Manager findFirstByUsername(String username);
    Manager findFirstByAgentId(String agentId);
    int countByWorkDayStatusAndRole(String dayStatus, String role);
}
