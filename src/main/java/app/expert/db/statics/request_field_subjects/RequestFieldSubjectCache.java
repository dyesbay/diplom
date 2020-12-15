package app.expert.db.statics.request_field_subjects;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class RequestFieldSubjectCache extends GRedisCache<RqFieldSubjectKey, RequestFieldSubject, RequestFieldSubjectRepository> {

    public RequestFieldSubjectCache(RequestFieldSubjectRepository repository) {
        super(repository, RequestFieldSubject.class);
    }

    public RequestFieldSubject findByRequestField(Long id) {
        return getRepository().findByRequestField(id);
    }

    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.RQ_FIELD_SUBJECT_ALREADY_EXISTS;
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.RQ_FIELD_SUBJECT_NOT_FOUND;
    }
}
