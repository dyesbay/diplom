package app.expert.db.schedule.manager_schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ManagerScheduleRepository extends JpaRepository<ManagerSchedule, Long>,
        JpaSpecificationExecutor<ManagerSchedule> {

    ManagerSchedule findByDate(Date date);

    ManagerSchedule findByDateAndManager(Date date, Long manager);

    List<ManagerSchedule> findByDateGreaterThanEqualAndDateLessThanAndManager(Date start, Date end, Long manager);

    @Query(value = "select COUNT(*) from managers_schedule ms INNER join managers m on ms.manager=m.id where ms.date " +
            "between :from AND :to AND ms.type='WORK_DAY' AND m.platform = :plat", nativeQuery = true)
    int countManagersWorkToday(@Param("from")Date from,
                               @Param("to")Date to,
                               @Param("plat") String platform);

}
