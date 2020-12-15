package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.models.GResponse;
import app.base.utils.DateUtils;
import app.expert.models.manager.schedule.RqEditManagerSchedule;
import app.expert.models.manager.schedule.RsManagerSchedule;
import app.expert.services.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api("3005. Управление расписанием сотрудника")
public class ManagerScheduleController extends GControllerAdvice {

    private final ScheduleService service;

    @PutMapping("/managerSchedule/all/addWeekends")
    @ApiOperation("Установить выходные дни для всех сотрудников")
    public GResponse addWeekendsForAll(@RequestParam @DateTimeFormat(pattern= DateUtils.HUMAN_DATE) Date from,
                                       @RequestParam @DateTimeFormat(pattern= DateUtils.HUMAN_DATE) Date to,
                                       @RequestParam @DateTimeFormat(pattern= DateUtils.HUMAN_DATE) List<Date> weekends) {
        return service.addWeekendsForAll(from, to, weekends);
    }

    @PutMapping("/managerSchedule/all/addWorkdays")
    @ApiOperation("Установить рабочие дни для всех сотрудников")
    public GResponse addWorkDaysForAll(@RequestParam @DateTimeFormat(pattern= DateUtils.HUMAN_DATE) Date from,
                                       @RequestParam @DateTimeFormat(pattern= DateUtils.HUMAN_DATE) Date to,
                                       @RequestParam @DateTimeFormat(pattern= DateUtils.HUMAN_DATE) List<Date> workdays) {
        return service.addWorkDaysForAll(from, to, workdays);
    }

    @PutMapping("/managerSchedule/edit")
    @ApiOperation("Редактировать график для сотрудника")
    public GResponse editManagerSchedule(@RequestBody @Validated RqEditManagerSchedule request) throws GNotFound, GNotAllowed {
        return service.editManagerSchedule(request);
    }

    @GetMapping("/managerSchedule/all")
    @ApiOperation("Запросить график сотрудников за месяц")
    public List<RsManagerSchedule> getSchedule(@RequestParam @DateTimeFormat(pattern= DateUtils.HUMAN_DATE) Date from,
                                         @RequestParam @DateTimeFormat(pattern= DateUtils.HUMAN_DATE) Date to) {

        return service.getSchedule(from, to);
    }
}
