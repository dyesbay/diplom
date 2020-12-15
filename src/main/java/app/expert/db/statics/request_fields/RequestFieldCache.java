package app.expert.db.statics.request_fields;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class RequestFieldCache extends GRedisCache<Long, RequestField, RequestFieldRepository> {

    public RequestFieldCache(RequestFieldRepository repository) {
        super(repository, RequestField.class);
    }
    
    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.REQUEST_FIELD_ALREADY_EXIST;
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.REQUEST_FIELD_NOT_FOUND;
    }
}
