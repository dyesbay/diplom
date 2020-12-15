package app.expert.db.schedule.general_schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GeneralScheduleRepository extends JpaRepository<GeneralSchedule, Long>,
        JpaSpecificationExecutor<GeneralSchedule> {

    GeneralSchedule findByDate(Date date);

    List<GeneralSchedule> findByDateGreaterThanEqualAndDateLessThan(Date start, Date end);
    List<GeneralSchedule> findByDateGreaterThanAndDateLessThan(Date start, Date end);
}
