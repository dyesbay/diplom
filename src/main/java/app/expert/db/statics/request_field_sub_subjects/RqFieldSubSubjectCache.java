package app.expert.db.statics.request_field_sub_subjects;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class RqFieldSubSubjectCache extends GRedisCache<RequestFieldSubSubjectKey, RequestFieldSubSubject, RqFieldSubSubjectRepository> {

    public RqFieldSubSubjectCache(RqFieldSubSubjectRepository repository) {
        super(repository, RequestFieldSubSubject.class);
    }

    public RequestFieldSubSubject findByRequestField(Long id){
        return getRepository().findByRequestField(id);
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.RQ_FIELD_SUB_SUBJECT_NOT_FOUND;
    }
}
