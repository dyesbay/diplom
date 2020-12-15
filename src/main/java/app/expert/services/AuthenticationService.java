package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GException;
import app.base.services.GContextService;
import app.base.utils.PasswordUtils;
import app.expert.constants.Configs;
import app.expert.constants.ExpertErrors;
import app.expert.db.configs.ConfigCache;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerRepository;
import app.expert.db.statics.manager_role.ManagerRole;
import app.expert.db.statics.manager_role.ManagerRoleCache;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.naming.ldap.LdapContext;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final GContextService contexts;
    private final ManagerRoleCache roleCache;
    private final ManagerRepository managerRepository;
    private final SessionService sessionService;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final ConfigCache configCache;


    public Manager auth(String username, String password) throws GBadRequest {
        username = username.toLowerCase();
        Manager manager = managerRepository.findFirstByUsername(username);

        if (manager != null && manager.getUsername().equals("superuser")) {

            String hash = PasswordUtils.generateUserPassword(password, Configs.SUPERUSER_SALT.getValue());
            if (!hash.equals(manager.getAgentPasswordHash())) {
                throw new GBadRequest();
            }

            contexts.setUser(manager.getId());

            sessionService.createOrUpdateSession(manager);
            return manager;
        }

        String URL;
        LdapContext ctx;
        ActiveDirectory.User user;
        String platform = "KURSK";
        try {
            String domain = configCache.find(Configs.ACTIVE_DIRECTORY_KURSK_DOMAIN.getKey()).getValue();
            URL = configCache.find(Configs.ACTIVE_DIRECTORY_SERVER_KURSK_1.getKey()).getValue();
            try {
                ctx = ActiveDirectory.getConnection(username, password, domain, URL);
                user = ActiveDirectory.getUser(username, ctx);
            } catch (Exception e) {

                URL = configCache.find(Configs.ACTIVE_DIRECTORY_SERVER_KURSK_2.getKey()).getValue();
                try {
                    ctx = ActiveDirectory.getConnection(username, password, domain, URL);
                    user = ActiveDirectory.getUser(username, ctx);
                } catch (Exception ex) {
                    domain = configCache.find(Configs.ACTIVE_DIRECTORY_KAZAN_DOMAIN.getKey()).getValue();
                    URL = configCache.find(Configs.ACTIVE_DIRECTORY_SERVER_KAZAN.getKey()).getValue();
                    ctx = ActiveDirectory.getConnection(username, password, domain, URL);
                    user = ActiveDirectory.getUser(username, ctx);
                    platform = "KAZAN";
                }
            }


            String role = null;

            for (String ou : user.getOu()) {
                for (ManagerRole r : roleCache.getAll()) {
                    if (ou.equals(r.getCode()))
                        role = r.getCode();
                }
            }

            if (manager == null)
                manager = managerRepository.save(
                        Manager.builder()
                                .username(username)
                                .email("")
                                .role(role)
                                .platform(platform)
                                .firstName(user.getGivenName())
                                .lastName(user.getSurname())
                                .registered(new Date())
                                .agentId(user.getPager())
                                .stationId(user.getIpPhone())
                                .build());
            ctx.close();
            contexts.setUser(manager.getId());

//             создать или обновить сессию
            sessionService.createOrUpdateSession(manager);

            return manager;
        } catch (Exception e) {
            logger.error("Failed to authenticate user", e);
            //Failed to authenticate user!
            throw new GBadRequest(ExpertErrors.BAD_CREDENTIALS);
        }
    }

    public void logout() throws GException {
        sessionService.closeSession();
    }


}
