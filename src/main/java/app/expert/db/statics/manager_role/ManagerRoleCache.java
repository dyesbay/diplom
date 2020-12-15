package app.expert.db.statics.manager_role;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class ManagerRoleCache extends GRedisCache<String, ManagerRole, ManagerRoleRepository> {

    public ManagerRoleCache(ManagerRoleRepository repository) {
        super(repository, ManagerRole.class);
    }
    
    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.MANAGER_ROLE_ALREADY_EXISTS;
    }
    
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.MANAGER_ROLE_NOT_FOUND;
    }
}
