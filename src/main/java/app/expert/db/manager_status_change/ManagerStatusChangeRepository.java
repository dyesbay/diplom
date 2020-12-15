package app.expert.db.manager_status_change;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ManagerStatusChangeRepository extends JpaRepository<ManagerStatusChange, Long> {

    ManagerStatusChange findFirstByManagerAndDateAndStatusAndEndNull(Long manager, Date date, String status);

    List<ManagerStatusChange> findByManagerAndStartGreaterThanEqualAndEndLessThanEqualOrEndNull(Long manager, Date start, Date end);

}
