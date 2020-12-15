package app.expert.db.request_reminders;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class RequestRemindersCache extends GRedisCache<Long , RequestReminder, RequestRemindersRepository> {

    public RequestRemindersCache(RequestRemindersRepository repository) {
        super(repository, RequestReminder.class);
    }
    
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.NOTIFICATION_NOT_FOUND;
    }
}
