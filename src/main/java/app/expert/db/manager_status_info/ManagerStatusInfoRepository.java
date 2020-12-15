package app.expert.db.manager_status_info;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ManagerStatusInfoRepository extends JpaRepository<ManagerStatusInfo, Long> {

    ManagerStatusInfo findFirstByManagerAndDate(Long manager, Date date);
}
