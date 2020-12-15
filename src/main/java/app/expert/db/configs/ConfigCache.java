package app.expert.db.configs;

import app.base.db.GRedisCache;
import org.springframework.stereotype.Service;

@Service
public class ConfigCache extends GRedisCache<String, Config, ConfigRepository> {

    public ConfigCache(ConfigRepository repository) {
        super(repository, Config.class);
    }

}
