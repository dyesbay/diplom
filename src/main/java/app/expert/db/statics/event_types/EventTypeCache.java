package app.expert.db.statics.event_types;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Component;

@Component
public class EventTypeCache extends GRedisCache<String, EventType, EventTypeRepository> {

    public EventTypeCache(EventTypeRepository repository) {
        super(repository, EventType.class);
    }

    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.EVENT_TYPE_ALREADY_EXIST;
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.EVENT_TYPE_NOT_FOUND;
    }
}
