package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.utils.EncryptionUtils;
import app.expert.constants.ExternalServicesCodes;
import app.expert.db.statics.integrations_configs.IntegrationConfig;
import app.expert.db.statics.integrations_configs.IntegrationConfigCache;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class ExternalService {

    private final IntegrationConfigCache integrationConfigCache;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    public String loginEsroo() throws GNotAllowed, GNotFound, GBadRequest, IOException {

        IntegrationConfig integrationConfig = integrationConfigCache.find(ExternalServicesCodes.ESROO.getKey());

        HashMap<String, String> map = new HashMap<>();
        map.put("login", integrationConfig.getUsername());
        String password = EncryptionUtils.decrypt(integrationConfig.getPasswordHash());
        map.put("password", password);
        HashMap response = app.expert.models.ExternalService.makePostRequest("https://" + integrationConfig.getServer() + "/user/login",
                map);
        String token = (String) response.get("token");
        integrationConfig.setKey(token);
        integrationConfigCache.save(integrationConfig);
        return token;
    }


    public String get() throws GNotAllowed, GNotFound, GBadRequest, IOException {

        String response = app.expert.models.ExternalService.makeGetRequest("https://google.com");
        return response;
    }
}
