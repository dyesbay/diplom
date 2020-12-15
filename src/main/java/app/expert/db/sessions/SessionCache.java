package app.expert.db.sessions;

import app.base.db.GRedisCache;
import app.base.exceptions.GNotFound;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionCache extends GRedisCache<Long, Session, SessionRepository> {

    public SessionCache(SessionRepository repository) {
        super(repository, Session.class);
    }

    public Session findByManager(Long manager) {
        return getRepository().findByManager(manager);
    }

    public List<Session> findByManagerUserName (String userName) throws GNotFound {
      return getRepository().findByManagerUserName(userName).orElseThrow(() -> new GNotFound(getNotFoundError()));
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.SESSION_NOT_FOUND;
    }
}
