package app.expert.db.event;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class EventCache extends GRedisCache<Long, Event, EventRepository> {

    public EventCache(EventRepository repository) {
        super(repository, Event.class);
    }
    
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.EVENT_NOT_FOUND;
    }
}
