package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.exceptions.GSystemError;
import app.base.models.GResponse;
import app.expert.db.managers_notifications.ManagersNotificationsCache;
import app.expert.models.managers_notifications.RqEditManagerNotification;
import app.expert.models.managers_notifications.RqManagerNotification;
import app.expert.models.managers_notifications.RqManagerNotificationFilter;
import app.expert.models.managers_notifications.RsManagerNotification;
import app.expert.services.ManagersNotificationsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Api(tags = "2030. Уведомления")
public class ManagersNotifications extends GControllerAdvice {

    private final ManagersNotificationsService service;
    private final ManagersNotificationsCache cache;

    @PutMapping("/managerNotification")
    @ApiOperation("Создать уведомление")
    public GResponse add(@RequestBody RqManagerNotification request)
            throws GNotFound, GNotAllowed, GSystemError {

        return service.add(request);
    }

    @GetMapping("/managerNotification")
    @ApiOperation("Получить уведомление по айди")
    public RsManagerNotification get(@RequestParam Long id) throws GNotAllowed, GNotFound {
        return RsManagerNotification.get(cache.find(id));
    }

    @PostMapping("/managerNotifications")
    @ApiOperation("Получить все уведомления с фильтром")
     public List<RsManagerNotification> getAll(@RequestBody RqManagerNotificationFilter filter) throws GNotAllowed, GNotFound {
        return service.getAllByFilter(filter);
    }

    @DeleteMapping("/managerNotification")
    @ApiOperation("Удалить уведомление")
    public GResponse delete(@RequestParam Long id) throws GNotAllowed, GSystemError, GNotFound {
        return service.disable(id);
    }


    @PatchMapping("/managerNotification")
    @ApiOperation("Изменить уведомление")
    public RsManagerNotification edit(@RequestBody RqEditManagerNotification request) throws GNotAllowed, GNotFound {
        return service.edit(request);
    }
}
