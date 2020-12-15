package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.exceptions.GNotFound;
import app.base.models.GResponse;
import app.expert.db.manager.Manager;
import app.expert.db.manager_status_info.ManagerStatusInfo;
import app.expert.db.manager_status_info.ManagerStatusInfoCache;
import app.expert.db.sessions.SessionCache;
import app.expert.db.statics.manager_role.ManagerRoleCache;
import app.expert.db.statics.manager_status.ManagerStatusCache;
import app.expert.models.RqAuth;
import app.expert.models.RsAuth;
import app.expert.services.AuthenticationService;
import app.expert.services.CookieService;
import com.google.common.net.HttpHeaders;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Api(tags = "1015. Авторизация")
public class Authorization extends GControllerAdvice {

    private final AuthenticationService authenticationService;
    private final CookieService cookieService;
    private final ManagerRoleCache managerRoleCache;
    private final ManagerStatusInfoCache statusInfo;
    private final ManagerStatusCache statusCache;
    private final SessionCache sessionCache;

    @PostMapping("/auth")
    private RsAuth auth(@RequestBody RqAuth auth, HttpServletResponse response) throws GException {
        Manager manager = authenticationService.auth(auth.getUsername(), auth.getPassword());
        cookieService.setUserCookie(response);
        String statusCode = null;
        String statusName = null;
        UUID uuid = null;
        try {
            ManagerStatusInfo info = statusInfo.findByManagerAndDate(manager.getId(), new Date());
            uuid = sessionCache.findByManager(manager.getId()).getUserUuid();
            statusCode = info == null ? null : info.getCurrentStatus();
            if (statusCode != null)
                statusName = statusCache.find(statusCode).getName();
        } catch (GNotFound ignored) {}
        return RsAuth.get(manager, managerRoleCache, response.getHeader(HttpHeaders.AUTHORIZATION), statusCode, statusName, uuid);
    }

    @PostMapping("/auth/logout")
    private GResponse logout(HttpServletResponse response) throws GException {
        authenticationService.logout();
        cookieService.setUserCookieExpired(response);
        return getResponse(GErrors.OK);
    }
}
