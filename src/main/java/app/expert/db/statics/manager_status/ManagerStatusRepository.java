package app.expert.db.statics.manager_status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerStatusRepository extends JpaRepository<ManagerStatus, String> {

    ManagerStatus findByWorkModeAndReasonCode(String workMode, String statusCode);

    ManagerStatus findByWorkMode(String workMode);
}
