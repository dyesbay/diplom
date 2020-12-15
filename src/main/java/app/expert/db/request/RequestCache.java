package app.expert.db.request;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class RequestCache extends GRedisCache<Long, Request, RequestRepository>{

    public RequestCache(RequestRepository repository) {
        super(repository, Request.class);
    }
    
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.REQUEST_NOT_FOUND;
    }
}
