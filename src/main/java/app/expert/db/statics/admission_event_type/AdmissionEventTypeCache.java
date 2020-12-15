package app.expert.db.statics.admission_event_type;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class AdmissionEventTypeCache extends GRedisCache<String, AdmissionEventType, AdmissionEventTypeRepository> {

    public AdmissionEventTypeCache(AdmissionEventTypeRepository repository) {
        super(repository, AdmissionEventType.class);
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.ADMISSION_EVENT_TYPE_NOT_FOUND;
    }
}
