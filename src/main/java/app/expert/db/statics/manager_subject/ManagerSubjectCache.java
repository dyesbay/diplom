package app.expert.db.statics.manager_subject;

import app.base.db.GRedisCache;
import org.springframework.stereotype.Service;

@Service
public class ManagerSubjectCache extends GRedisCache<ManagerSubjectKey, ManagerSubject, ManagerSubjectRepository> {

    public ManagerSubjectCache(ManagerSubjectRepository repository) {
        super(repository, ManagerSubject.class);
    }

}
