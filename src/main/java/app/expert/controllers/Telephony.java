package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.expert.services.SessionService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
@RequiredArgsConstructor
@Api(tags = "--- Телефония")
public class Telephony extends GControllerAdvice {

    private final SessionService sessionService;

    @PatchMapping("/telephony")
    public Long addStationToSession(String agentId, Long stationId) throws GException {
        return sessionService.addStationToSession(agentId, stationId);
    }
}
