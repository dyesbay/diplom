package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.db.statics.services.Service;
import app.expert.services.ServicesManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/static")
@RequiredArgsConstructor
@Api(tags = "1020. Сервисы")
public class Services extends GControllerAdvice {

    private final ServicesManager service;
    
    @GetMapping("/service")
    @ApiOperation("Получить сервис по коду")
    public Service get(
            @RequestParam(required = true) String code) throws GException {
        return service.get(code);
    }

    @PutMapping("/service")
    @ApiOperation("Добавить новый сервис")
    public Service add(
            @RequestParam String code,
            @RequestParam String name,   
            @RequestParam(required = false) String description) throws GException {
        return service.add(code, name, description);
    }

    @PatchMapping("/service")
    @ApiOperation("Изменение существующего сервиса")
    public Service edit(
            @RequestParam String code, 
            @RequestParam String name,
            @RequestParam(required = false) String description) throws GException {
        return service.edit(code, name, description);
    }

    @DeleteMapping("/service")
    @ApiOperation("Удалить сервис")
    public GResponse delete(
            @RequestParam String code) throws GException {
        service.delete(code);
        return new GResponse(GErrors.OK);
    }

    @GetMapping("/services")
    @ApiOperation("Получить все сервисы")
    public List<Service> getAll() {
        return service.getAll();
    }
}
