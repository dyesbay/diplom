package app.expert.db.statics.subjects;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class SubjectCache extends GRedisCache<Long, Subject, SubjectRepository>{

    public SubjectCache(SubjectRepository repository) {
        super(repository, Subject.class);
    }

    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.SUBJECT_ALREADY_EXISTS;
    }
    
    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.SUBJECT_NOT_FOUND_ERROR;
    }
}
