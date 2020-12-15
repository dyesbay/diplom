package app.expert.db.schedule.general_schedule;

import app.base.db.GRedisCache;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GeneralScheduleCache extends GRedisCache<Long, GeneralSchedule, GeneralScheduleRepository> {

    public GeneralScheduleCache(GeneralScheduleRepository repository) {
        super(repository, GeneralSchedule.class);
    }

    public GeneralSchedule findByDate(Date date) {
        return getRepository().findByDate(date);
    }
}
