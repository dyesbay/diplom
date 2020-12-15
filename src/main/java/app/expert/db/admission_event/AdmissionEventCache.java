package app.expert.db.admission_event;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdmissionEventCache extends GRedisCache<Long, AdmissionEvent, AdmissionEventRepository> {

    public AdmissionEventCache(AdmissionEventRepository repository) {
        super(repository, AdmissionEvent.class);
    }

    public List<AdmissionEvent> findByAdmission(Long id){
        return getRepository().findByAdmission(id);
    }

    public List<AdmissionEvent> findByAdmission(Sort sort, Long id){
        return getRepository().findByAdmission(sort, id);
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.ADMISSION_EVENT_NOT_FOUND;
    }

}
