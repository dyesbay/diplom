package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.db.statics.status.Status;
import app.expert.services.StatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/static")
@Api(tags = "1016. Статусы")
@RequiredArgsConstructor
public class Statuses extends GControllerAdvice {

    private final StatusService service;
    
    @GetMapping("/status")
    @ApiOperation("Запросить статус по коду")
    public Status get(@RequestParam String code) throws GException {
        return service.get(code);
    }

    @PutMapping("/status")
    @ApiOperation("Добавить новый статус")
    public Status add(
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam(required = false) String description
    ) throws GException {
        return service.add(code, name, description);
    }

    @PatchMapping("/status")
    @ApiOperation("Изменение существующего статуса")
    public Status edit(
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam(required = false) String description
    ) throws GException {
        return service.edit(code, name, description);
    }

    @DeleteMapping("/status")
    @ApiOperation("Удалить статус")
    public GResponse delete(@RequestParam String code) throws GException {
        service.delete(code);
        return new GResponse(GErrors.OK);
    }

    @GetMapping("/statuses")
    @ApiOperation("Запросить все статусы")
    public List<Status> getAll() throws GException {
        return service.getAll();
    }
}
