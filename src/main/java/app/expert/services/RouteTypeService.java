package app.expert.services;

import app.base.exceptions.GException;
import app.expert.db.statics.route_type.RouteType;
import app.expert.db.statics.route_type.RouteTypeCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteTypeService {

    private final RouteTypeCache cache;

    public RouteType get(final String id) throws GException {
        return cache.find(id);
    }

    public RouteType add(String code, String name, String description) throws GException {
        return cache.save(RouteType.builder()
                .code(code)
                .name(name)
                .description(description)
                .build());
    }

    public RouteType update(String code, String name, String description) throws GException {
        RouteType type = cache.find(code);
        type.setDescription(description);
        type.setName(name);
        return cache.save(type);
    }

    public void delete(String code) throws GException {
        RouteType type = cache.find(code);
//        type.setDisabledOn(new Date());
        cache.save(type);
    }
}
