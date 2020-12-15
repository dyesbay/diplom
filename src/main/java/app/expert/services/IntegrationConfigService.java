package app.expert.services;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.constants.ExternalServicesCodes;
import app.expert.db.statics.integrations_configs.IntegrationConfig;
import app.expert.db.statics.integrations_configs.IntegrationConfigCache;
import app.expert.models.RqIntegrationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntegrationConfigService {

    private final IntegrationConfigCache cache;

    public IntegrationConfig add(RqIntegrationConfig request) {

        //TODO обработать коды в зависимости от сервера
        return cache.save(IntegrationConfig.getFromRequest(request,
                ExternalServicesCodes.ACTIVE_DIRECTORY.getKey()));
    }

    public IntegrationConfig getIntegrationConfig(ExternalServicesCodes serviceCode) throws GNotAllowed, GNotFound {
        return cache.find(serviceCode.getKey());
    }
}
