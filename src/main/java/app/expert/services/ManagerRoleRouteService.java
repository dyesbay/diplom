package app.expert.services;

import app.base.exceptions.GException;
import app.expert.db.statics.manager_role.ManagerRoleCache;
import app.expert.db.statics.manager_role_route.ManagerRoleRoute;
import app.expert.db.statics.manager_role_route.ManagerRoleRouteCache;
import app.expert.db.statics.manager_role_route.ManagerRoleRouteKey;
import app.expert.db.statics.manager_role_route.ManagerRoleRouteRepository;
import app.expert.db.statics.route.RouteCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ManagerRoleRouteService {
    
    private final ManagerRoleRouteCache cache;
    private final ManagerRoleRouteRepository repo;
    private final RouteCache routeCache;
    private final ManagerRoleCache managerRoleCache;
    
    public ManagerRoleRoute get(String code, Long id) throws GException {
        return cache.find(ManagerRoleRouteKey.builder().managerCode(code).routeId(id).build()); 
    }
    
    public List<ManagerRoleRoute> getByRouteId(Long id) throws GException {
        routeCache.find(id);
        return repo.findByRouteId(id);
    }
    
    public List<ManagerRoleRoute> getByManagerRole(String code) throws GException {
        managerRoleCache.find(code);
        return repo.findByManagerRoleCode(code);
    }
    
    public ManagerRoleRoute add(String code, Long id) throws GException {
        return cache.save(
                ManagerRoleRoute.builder()
                .managerRole(managerRoleCache.find(code))
                .route(routeCache.find(id))
                .key(new ManagerRoleRouteKey(code, id))
                .build());
    }

    public void deleteRelation(String code, Long id) throws GException {
        cache.delete(new ManagerRoleRouteKey(code, id));
    }
}
