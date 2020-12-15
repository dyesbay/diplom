package app.expert.db.admission;

import app.base.db.GRedisCache;
import app.base.exceptions.GNotFound;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class AdmissionCache extends GRedisCache<Long, Admission, AdmissionRepository>{

    public AdmissionCache(AdmissionRepository repository) {
        super(repository, Admission.class);
    }

    public Admission findByCall(Long call) throws GNotFound {
        return getRepository().findByCall(call)
                .orElseThrow(() -> new GNotFound(getAlreadyExistsError()));
    }
    
    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.ADMISSION_ALREADY_EXISTS;
    }
    
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.ADMISSION_NOT_FOUND;
    }

}
