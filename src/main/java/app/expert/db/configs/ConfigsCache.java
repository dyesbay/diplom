package app.expert.db.configs;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class ConfigsCache extends GRedisCache<String, Config, ConfigsRepository> {

    public ConfigsCache(ConfigsRepository repository) {
        super(repository, Config.class);
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.CONFIG_NOT_FOUND;
    }

    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.CONFIG_ALREADY_EXISTS;
    }
}
