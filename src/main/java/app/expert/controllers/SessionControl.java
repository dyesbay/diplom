package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.sessions.Session;
import app.expert.db.sessions.SessionCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Api(tags = "Действия над сессиями")
public class SessionControl extends GControllerAdvice {

    private final ManagerCache managerCache;
    private final SessionCache sessionCache;

    @PatchMapping("/session")
    @ApiOperation("Прервать сессию сотрудника")
    public GResponse interruptSession(@RequestParam String username) throws GException {
        Manager manager = managerCache.findByUsername(username);
        List<Session> sessions = sessionCache.findByManagerUserName(username);
        for(Session session : sessions) {
            sessionCache.delete(session.getId());
        }
        return new GResponse(GErrors.OK);
    }
}
