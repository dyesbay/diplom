package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.exceptions.GSystemError;
import app.base.models.GResponse;
import app.expert.models.request.RqGetReminderByDate;
import app.expert.models.request.RqRequestReminder;
import app.expert.models.request.RqRequestReminderPostpone;
import app.expert.models.request.RsRequestReminder;
import app.expert.services.RequestReminderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ControllerAdvice
@Api(tags = "1028. Напоминания для ОЗ")
@RequiredArgsConstructor
public class RequestReminders extends GControllerAdvice {
    
    private final RequestReminderService service;

    @GetMapping("/requestReminder")
    @ApiOperation("Запросить по айди запроса")
    public List<RsRequestReminder> getByRequest(@RequestParam Long request) {

        return service.getByRequest(request);
    }

    @GetMapping("/requestReminder/getByDate")
    @ApiOperation("Запросить по времени")
    public List<RsRequestReminder> getByDate(@RequestBody RqGetReminderByDate rq) {

        return service.getByDate(rq.getDate());
    }

    @PutMapping("/requestReminder")
    @ApiOperation("Добавить напоминание")
    public RsRequestReminder put(@Validated @RequestBody RqRequestReminder rq)
            throws GNotAllowed, GNotFound, GBadRequest, GSystemError {

        return service.add(rq);
    }
    
    @PatchMapping("/requestReminder/postpone")
    @ApiOperation("Отложить уведомление")
    public RsRequestReminder postpone(@Validated @RequestBody RqRequestReminderPostpone rq)
            throws GNotAllowed, GNotFound, GBadRequest, GSystemError {
        return service.postpone(rq);
    }

    @DeleteMapping("/requestReminder")
    @ApiOperation("Отключить уведомление")
    public GResponse disable(@RequestParam Long id) throws GNotAllowed, GNotFound, GSystemError {
        return service.disable(id);
    }
}
