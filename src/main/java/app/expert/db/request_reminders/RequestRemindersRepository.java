package app.expert.db.request_reminders;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface RequestRemindersRepository extends JpaRepository<RequestReminder, Long> {

    int countBySignalBetween(Date from, Date to);

    List<RequestReminder> findAllByRequestAndCheckedFalse(Long request);

    List<RequestReminder> findAllBySignalAndCheckedFalse(Date date);
}
