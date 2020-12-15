package app.expert.db.statics.manager_role_route;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class ManagerRoleRouteCache extends GRedisCache<ManagerRoleRouteKey, ManagerRoleRoute, ManagerRoleRouteRepository>{

    public ManagerRoleRouteCache(ManagerRoleRouteRepository repository) {
        super(repository, ManagerRoleRoute.class);
    }
    
    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.MANAGER_ROLE_ROUTE_ALREADY_EXISTS;
    }
    
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.MANAGER_ROLE_ROUTE_NOT_FOUND;
    }
}
