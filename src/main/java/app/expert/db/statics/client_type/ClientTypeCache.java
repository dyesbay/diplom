package app.expert.db.statics.client_type;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Component;

@Component
public class ClientTypeCache extends GRedisCache<String, ClientType, ClientTypeRepository> {

    public ClientTypeCache(ClientTypeRepository repository) {
        super(repository, ClientType.class);
    }

    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.CLIENT_TYPE_ALREADY_EXISTS;
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.CLIENT_TYPE_NOT_FOUND;
    }

}
