package app.expert.db.manager;

import app.base.db.GRedisCache;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class ManagerCache extends GRedisCache<Long, Manager, ManagerRepository>{

    public ManagerCache(ManagerRepository repository) {
        super(repository, Manager.class);
    }
    
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.MANAGER_NOT_FOUND;
    }
    
    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.MANAGER_ALREADY_EXISTS;
    }

    //#TODO разобраться с созданием пользователя
    public Manager findByUsernameDisabled(String username) throws GNotFound, GNotAllowed {
        return getRepository().findFirstByUsername(username);
    }

    public Manager findByUsername(String username) throws GNotFound, GNotAllowed {
        Manager manager = findByUsernameDisabled(username);
        if (manager == null) throw new GNotFound(ExpertErrors.MANAGER_NOT_FOUND);
        if (manager.isDisabled()) throw new GNotAllowed(getDisabledError());
        return manager;
    }


    public Manager findByAgentId(String agentId) throws GNotFound, GNotAllowed {
        Manager manager = getRepository().findFirstByAgentId(agentId);
        if (manager == null) throw new GNotFound(ExpertErrors.MANAGER_NOT_FOUND);
        if (manager.isDisabled()) throw new GNotAllowed(getDisabledError());
        return manager;
    }
}
