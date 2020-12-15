package app.expert.db.statics.integrations_configs;

import app.base.db.GRedisCache;
import org.springframework.stereotype.Component;

@Component
public class IntegrationConfigCache extends GRedisCache<String, IntegrationConfig, IntegrationConfigRepository> {

    public IntegrationConfigCache(IntegrationConfigRepository repository) {
        super(repository, IntegrationConfig.class);
    }
}
