package app.expert.db.schedule.manager_schedule;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ManagerScheduleCache extends GRedisCache<Long, ManagerSchedule, ManagerScheduleRepository> {

    public ManagerScheduleCache(ManagerScheduleRepository repository) {
        super(repository, ManagerSchedule.class);
    }

    public ManagerSchedule findByDate(Date date) {
        return getRepository().findByDate(date);
    }

    public IGEnum getAlreadyExistsError() {
        return super.getAlreadyExistsError();
    }
}
