package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.models.GResponse;
import app.expert.constants.Platform;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.models.manager.RQCreateManager;
import app.expert.models.manager.RQEditManager;
import app.expert.models.manager.RSManager;
import app.expert.models.manager.*;
import app.expert.services.ManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static app.base.constants.GErrors.OK;
import static app.base.utils.DateUtils.HUMAN_DATE;

@RequiredArgsConstructor
@RestController
@Api(tags = "1004. Сотрудники")
public class Managers extends GControllerAdvice {

    private final ManagerService service;
    private final ManagerCache cache;

    @GetMapping("/manager")
    @ApiOperation("Получить сотрудника по id")
    public RSManager get(@RequestParam Long id) throws GException {
        return service.get(id);
    }

    @GetMapping("/managers")
    @ApiOperation("Получить список сотрудников")
    public List<RSManager> get(String role, Platform platform) {
        return service.getAll(role, platform);
    }

    @PutMapping("/manager")
    @ApiOperation("Добавить нового сотрудника")
    public RSManager add(@Validated @RequestBody RQCreateManager managerInfo) throws GException {
        return service.add(managerInfo);
    }

    @PatchMapping("/manager")
    @ApiOperation("Обновить данные сотрудника (имя, почту, платформу, роль")
    public RSManager update(@Validated @RequestBody RQEditManager managerInfo) throws GException {
        return service.update(managerInfo);
    }

    @DeleteMapping("/manager")
    @ApiOperation("Удалить сотрудника")
    public GResponse delete(@RequestParam Long id) throws GException {
        service.delete(id);
        return new GResponse(OK);
    }

    @PatchMapping("/manager/stationInfo")
    @ApiOperation("Задать stationInfo для менеджера")
    public RSManager setStationInfo(@RequestBody RqStationInfo stationInfo) throws GException {
        return service.addStationInfo(stationInfo);
    }

        @PatchMapping("/manager/dateOfDismissal")
    @ApiOperation("Указать дату увольнения")
    public GResponse setDateOfDismissal(@RequestParam Long managerId,
                                        @RequestParam @DateTimeFormat(pattern = HUMAN_DATE) Date date) throws GNotAllowed, GNotFound {
        Manager manager = cache.find(managerId);
        manager.setDateOfDismissal(date);
        cache.save(manager);
        return new GResponse(OK);
    }

    @GetMapping("/manager/stat/today")
    @ApiOperation("Получить статистику сотрудника на сегодня")
    public RsTodayManagerStat getTodayManagerStat(@RequestParam(required = false) Long id) throws GNotFound, GNotAllowed {
        return service.todayManagerStatistics(id);
    }
}
