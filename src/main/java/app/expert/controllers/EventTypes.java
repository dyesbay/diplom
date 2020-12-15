package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.db.statics.event_types.EventType;
import app.expert.models.RqEventType;
import app.expert.services.EventTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/static")
@RequiredArgsConstructor
@Api(tags = "1026. Типы событий")
public class EventTypes extends GControllerAdvice {
    
    private final EventTypeService service;

    @GetMapping("/eventType")
    @ApiOperation("Получить тип события по коду")
    public EventType get(@RequestParam String code) throws GException {
        return service.get(code);
    }

    @PutMapping("/eventType")
    @ApiOperation("Добавить новый тип события")
    public EventType add(@Validated @ModelAttribute RqEventType rq) throws GException {
        return service.add(rq);
    }

    @PatchMapping("/eventType")
    @ApiOperation("Изменение существующего типа события")
    public EventType edit(@Validated @ModelAttribute RqEventType rq) throws GException {
        return service.edit(rq);
    }

    @DeleteMapping("/eventType")
    @ApiOperation("Удалить тип события")
    public GResponse delete(@RequestParam String code) throws GException {
        service.delete(code);
        return new GResponse(GErrors.OK);
    }

    @GetMapping("/eventTypes")
    @ApiOperation("Получить все типы события")
    public List<EventType> getAll() {
        return service.getAll();
    }
}
