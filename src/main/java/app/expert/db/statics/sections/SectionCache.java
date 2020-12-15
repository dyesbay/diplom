package app.expert.db.statics.sections;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class SectionCache extends GRedisCache<Long, Section, SectionRepository> {

    public SectionCache(SectionRepository repository) {
        super(repository, Section.class);
    }

    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.SECTION_ALREADY_EXISTS;
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.SECTION_NOT_FOUND;
    }
}
