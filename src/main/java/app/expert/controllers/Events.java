package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.models.RsEvent;
import app.expert.services.EventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "1027. События")
public class Events extends GControllerAdvice {

    private final EventService service;

    @GetMapping("/events")
    @ApiOperation("получить историю событий по запросу")
    public List<RsEvent> getAll(@RequestParam Long request) throws GNotAllowed, GNotFound {
        return service.getByRequest(request);
    }

    @GetMapping("/event")
    @ApiOperation("получить событие по айди")
    public RsEvent get(@RequestParam Long id) throws GNotAllowed, GNotFound {
        return service.read(id);
    }
}
