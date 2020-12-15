package app.expert.db.statics.route;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class RouteCache extends GRedisCache<Long, Route, RouteRepository>{

    public RouteCache(RouteRepository repository) {
        super(repository, Route.class);
    }
        
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.ROUTE_NOT_FOUND;
    }
}
