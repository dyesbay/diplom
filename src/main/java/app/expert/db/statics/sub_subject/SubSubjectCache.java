package app.expert.db.statics.sub_subject;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class SubSubjectCache extends GRedisCache<Long, SubSubject, SubSubjectRepository> {

    public SubSubjectCache(SubSubjectRepository repository) {
        super(repository, SubSubject.class);
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.SUB_SUBJECT_NOT_FOUND;
    }
}
