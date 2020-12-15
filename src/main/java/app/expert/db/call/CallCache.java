package app.expert.db.call;

import app.base.db.GRedisCache;
import app.base.exceptions.GNotFound;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CallCache extends GRedisCache<Long, Call, CallRepository> {

    public CallCache(CallRepository repository) {
        super(repository, Call.class);
    }

    public Call getByCallId(String callId) throws GNotFound {
        return getRepository().findByIdentifier(callId).orElseThrow(() -> new GNotFound(getNotFoundError()));
    }

    public List<Call> getByManager(Long id) {
        return getRepository().findByManager(id);
    }
    
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.CALL_NOT_FOUND;
    }
    
    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.CALL_ALREADY_EXISTS;
    }
    
    public IGEnum getAlreadyComplete() {
        return ExpertErrors.CALL_WAS_COMPLETED;
    }
}
