package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.exceptions.GSystemError;
import app.expert.models.manager.RSStatusChange;
import app.expert.models.manager.RSStatusInfo;
import app.expert.services.StatusChangeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/statusChange")
@Api(tags = "1036. Изменение статусов сотрудника")
public class StatusChanges extends GControllerAdvice {

    private final StatusChangeService service;

    @PatchMapping
    @ApiOperation("Изменить статус сотрудника")
    public RSStatusInfo setStatus(String username, String agentId, String reasonCode, String workMode)
            throws GSystemError, GNotAllowed, GBadRequest, GNotFound {

        return service.setStatus(username, agentId, reasonCode, workMode);
    }

    @GetMapping("/info")
    @ApiOperation("Запросить суммарное время для каждого статуса сотрудника за сегодня")
    public RSStatusInfo getStatusInfo(@RequestParam String username) throws GNotAllowed, GNotFound, GSystemError {
        return service.getStatusInfo(username);
    }

    @GetMapping("/infoInInterval")
    @ApiOperation("Запросить историю изменений статусов" +
            " сотрудника в указанном временном промежутке")
    public List<RSStatusChange> getStatusInfoInInterval(
            @RequestParam String username,
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") Date start,
            @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") Date end) throws GNotAllowed, GNotFound, GSystemError {

        return service.getStatusInfoInInterval(username, start, end);
    }

}
