package app.expert.services;

import app.base.constants.GErrors;
import app.base.exceptions.GAlreadyExists;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.utils.ObjectUtils;
import app.expert.db.statics.manager_role.ManagerRole;
import app.expert.db.statics.manager_role.ManagerRoleCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class ManagerRoleService {
    
    private final ManagerRoleCache cache;

    public ManagerRole get(String code) throws GNotAllowed, GNotFound {
        return cache.find(code);
    }

    public ManagerRole add(String code, String name, String description) throws GNotAllowed, GNotFound, GBadRequest, GAlreadyExists {
        if (ObjectUtils.isBlank(code)) throw new GBadRequest(GErrors.BAD_REQUEST);
        if (ObjectUtils.isBlank(name)) throw new GBadRequest(GErrors.BAD_REQUEST);
        cache.checkIfNotExists(code);
        return cache.save(ManagerRole.builder()
                .code(code)
                .name(name)
                .description(description)
                .build());
        
    }

    public void delete(String code) throws GNotAllowed, GNotFound {
        ManagerRole role = cache.find(code);
        role.setDisabled(new Date());
        cache.save(role);
    }

    public ManagerRole update(String code, String name, String description) throws GNotAllowed, GNotFound {
        ManagerRole role = cache.find(code);
        role.setCode(code);
        role.setName(name);
        role.setDescription(description);
        return cache.save(role);
    }
}
