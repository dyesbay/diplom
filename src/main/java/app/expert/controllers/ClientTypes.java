package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.db.statics.client_type.ClientType;
import app.expert.services.ClientTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/static")
@Api(tags = "1018. Типы клиентов")
public class ClientTypes extends GControllerAdvice {

    private final ClientTypeService service;
    
    @GetMapping("/clientType")
    @ApiOperation("Получить тип клиента по коду")
    public ClientType get(
            @RequestParam String code) throws GException {
        return service.get(code);
    }

    @PutMapping("/clientType")
    @ApiOperation("Добавить новый тип клиента")
    public ClientType add(
            @RequestParam String code,
            @RequestParam String name,   
            @RequestParam String description) throws GException {
        return service.add(code, name, description);
    }

    @PatchMapping("/clientType")
    @ApiOperation("Изменение существующего типа клиента")
    public ClientType edit(
            @RequestParam String code, 
            @RequestParam String name,
            @RequestParam String description) throws GException {
        return service.edit(code, name, description);
    }

    @DeleteMapping("/clientType")
    @ApiOperation("Удалить тип клиента")
    public GResponse delete(
            @RequestParam String code) throws GException {
        service.delete(code);
        return new GResponse(GErrors.OK);
    }

    @GetMapping("/clientTypes")
    @ApiOperation("Получить все типы клиента")
    public List<ClientType> getAll() {
        return service.getAll();
    }
}
