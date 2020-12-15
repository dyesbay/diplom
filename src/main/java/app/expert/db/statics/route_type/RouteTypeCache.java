package app.expert.db.statics.route_type;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class RouteTypeCache extends GRedisCache<String, RouteType, RouteTypeRepository> {

    public RouteTypeCache(RouteTypeRepository repository) {
        super(repository, RouteType.class);
    }
    
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.ROUTE_TYPE_NOT_FOUND;
    }
}
