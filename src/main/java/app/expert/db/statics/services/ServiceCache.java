package app.expert.db.statics.services;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Component;

@Component
public class ServiceCache extends GRedisCache<String, Service, ServiceRepository>{
    
    public ServiceCache(ServiceRepository repository) {
        super(repository, Service.class);
    }

    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.SERVICE_ALREADY_EXISTS;
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.SERVICE_NOT_FOUND;
    }
}
