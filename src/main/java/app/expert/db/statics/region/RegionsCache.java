package app.expert.db.statics.region;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class RegionsCache extends GRedisCache<Long, Region, RegionsRepository> {

    public RegionsCache(RegionsRepository repository) {
        super(repository, Region.class);
    }

    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.REGION_ALREADY_EXISTS;
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.REGION_NOT_FOUND;
    }
}
