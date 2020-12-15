package app.expert.db.statics.status;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Component;

@Component
public class StatusCache extends GRedisCache<String, Status, StatusRepository> {

    public StatusCache(StatusRepository repository) {
        super(repository, Status.class);
    }

    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.STATUS_ALREADY_EXISTS;
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.STATUS_NOT_FOUND;
    }
}
