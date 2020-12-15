package app.expert.db.statics.manager_status;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Component;

@Component
public class ManagerStatusCache extends GRedisCache<String, ManagerStatus, ManagerStatusRepository> {

    public ManagerStatusCache(ManagerStatusRepository repository) {
        super(repository, ManagerStatus.class);
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
