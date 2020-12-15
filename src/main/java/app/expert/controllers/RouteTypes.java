package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.db.statics.route_type.RouteType;
import app.expert.services.RouteTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "1017. Типы маршрутов")
@RequestMapping("/static")
@RequiredArgsConstructor
public class RouteTypes extends GControllerAdvice {
    
    private final RouteTypeService service;
    
    @GetMapping("/routeType")
    @ApiOperation("Получить тип маршрута по id")
    public RouteType get(@RequestParam String code) throws GException {
        return service.get(code);
    }
    
    @PutMapping("/routeType")
    @ApiOperation("Добавить тип маршрут")
    public RouteType add(@RequestParam String code,
                    @RequestParam String name,
                    @RequestParam(required = false) String description) throws GException {
        return service.add(code, name, description);
    }
    
    @PatchMapping("/routeType")
    @ApiOperation("Обновить тип маршрута")
    public RouteType update(@RequestParam String code,
                    @RequestParam String name,
                    @RequestParam(required = false) String description) throws GException {
        return service.update(code, name, description);
    }
    
    @DeleteMapping("/routeType")
    @ApiOperation("Удалить тип маршрута") 
    public GResponse delete(@RequestParam String code) throws GException {
        service.delete(code);
        return new GResponse(GErrors.OK);
    }
}
