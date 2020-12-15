package app.expert.services;

import app.base.exceptions.GException;
import app.expert.db.statics.route.Route;
import app.expert.db.statics.route.RouteCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RouteService {

    private final RouteCache cache;

    public Route add(Route route) {
        return cache.save(route);
    }
    
    public Route get(Long id) throws GException {
        return cache.find(id);
    }

    public Route update(Route route) throws GException {
        Route oldRoute = cache.find(route.getId());
        oldRoute.setId(route.getId());
        oldRoute.setPath(route.getPath());
        oldRoute.setMethod(route.getMethod());
        oldRoute.setOpen(route.getOpen());
        oldRoute.setSystem(route.getSystem());
        return cache.save(route);
    }
    
    public void delete(Long id) throws GException {
        Route route = cache.find(id);
        route.setDisabled(new Date());
        cache.save(route);
    }

    public Optional<Route> findByMethodAndPath(String method, String path) {
        return cache.getAll().stream()
                .filter(x -> x.getPath().equals(path) && x.getMethod().equals(method)).findFirst();
    }
}
