package app.expert.configs;

import app.base.RedisService;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.utils.EncryptionUtils;
import app.base.utils.PasswordUtils;
import app.expert.constants.ConfigCode;
import app.expert.constants.Configs;
import app.expert.constants.ExternalServicesCodes;
import app.expert.db.configs.Config;
import app.expert.db.configs.ConfigCache;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.statics.integrations_configs.IntegrationConfig;
import app.expert.db.statics.integrations_configs.IntegrationConfigCache;
import app.expert.parsers.file_parser.FileLoader;
import app.expert.parsers.weekends_parser.WeekendsParser;
import app.expert.services.NamesLoaderService;
import app.expert.services.SubjectsAccessService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Configuration
public class GApplicationStart implements ApplicationListener<ContextRefreshedEvent> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${info.app.name}")
    protected String system;

    @Value("${info.app.timezone:Europe/Moscow}")
    protected String timezone;

    private final RedisService redisService;
    private final WeekendsParser weekendsParser;

    private final NamesLoaderService namesLoaderService;
    private final SubjectsAccessService subjectsAccessService;
    private final ConfigCache configCache;
    private final FileLoader fileLoader;
    private final ManagerCache managerCache;
    private final IntegrationConfigCache integrationConfigCache;

    protected void clearCache() {
        Set<String> keys = redisService.keys(system + ".*");
        logger.info("Clearing static cache: {}", keys);
        redisService.del(keys.toArray(new String[0]));
    }

    private void addConfigs() {
        logger.info("Adding default configs");
        configCache.save(Config.builder().key(ConfigCode.REQUEST_DISTRIBUTION.getKey()).value("0.5").build());
        configCache.save(Config.builder().key(Configs.ACTIVE_DIRECTORY_KURSK_DOMAIN.getKey())
                .value(Configs.ACTIVE_DIRECTORY_KURSK_DOMAIN.getValue()).build());
        configCache.save(Config.builder().key(Configs.ACTIVE_DIRECTORY_KAZAN_DOMAIN.getKey())
                .value(Configs.ACTIVE_DIRECTORY_KAZAN_DOMAIN.getValue()).build());
        configCache.save(Config.builder().key(Configs.ACTIVE_DIRECTORY_SERVER_KURSK_1.getKey())
                .value(Configs.ACTIVE_DIRECTORY_SERVER_KURSK_1.getValue()).build());
        configCache.save(Config.builder().key(Configs.ACTIVE_DIRECTORY_SERVER_KURSK_2.getKey())
                .value(Configs.ACTIVE_DIRECTORY_SERVER_KURSK_2.getValue()).build());
        configCache.save(Config.builder().key(Configs.ACTIVE_DIRECTORY_SERVER_KAZAN.getKey())
                .value(Configs.ACTIVE_DIRECTORY_SERVER_KAZAN.getValue()).build());
    }

    private void addSuperuser() {
        try {
            managerCache.findByUsername("superuser");
        } catch (GNotFound| GNotAllowed e) {
            Manager superuser = Manager.builder()
                    .username("superuser")
                    .firstName("super")
                    .lastName("user")
                    .role("admin")
                    .platform("KURSK")
                    .agentPasswordHash(PasswordUtils
                            .generateUserPassword("XSW@1qaz", Configs.SUPERUSER_SALT.getValue()))
                    .build();
            managerCache.save(superuser);
        }
    }

    private void addIntegrationsConfigs() {

        try {
            integrationConfigCache.find(ExternalServicesCodes.ESROO.getKey());
        } catch (GNotAllowed|GNotFound e) {
            String pass = null;
            try {
                pass = EncryptionUtils.encrypt("JgXITd");
            } catch (GBadRequest ignore) {}
            integrationConfigCache.save(IntegrationConfig.builder()
                    .code(ExternalServicesCodes.ESROO.getKey())
                    .server("10.132.21.17/api/v1")
                    .username("inline")
                    .passwordHash(pass)
                    .serviceName("esroo")
                    .build());
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        try {
            clearCache();
            TimeZone.setDefault(TimeZone.getTimeZone(timezone));
            CompletableFuture.runAsync(namesLoaderService::loadNames);
            CompletableFuture.runAsync(namesLoaderService::loadSurnames);
            CompletableFuture.runAsync(subjectsAccessService::addDefaultSubjectConfigsForAll);
            CompletableFuture.runAsync(this::addConfigs);
            CompletableFuture.runAsync(fileLoader::loadFileFromResource);
            CompletableFuture.runAsync(this::addSuperuser);
            CompletableFuture.runAsync(weekendsParser::parse);
            CompletableFuture.runAsync(this::addIntegrationsConfigs);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }
}
