package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.exceptions.GNotFound;
import app.base.models.GResponse;
import app.expert.models.request.RqRequestField;
import app.expert.models.request.RsRequestField;
import app.expert.services.RequestFieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@ControllerAdvice
@Api(tags = "1013. Формы запросов для тем обращений")
@RequestMapping("/static")
public class RequestFields extends GControllerAdvice {

    private final RequestFieldService service;

    @GetMapping("/requestField")
    @ApiOperation("Получить форму запроса по id")
    public RsRequestField get(@RequestParam Long id) throws GException {
        return service.get(id);
    }

    @PutMapping("/requestField")
    @ApiOperation("Добавить поле запроса к теме или подтематике обращения")
    public RsRequestField add(@ModelAttribute(name = "rqRequestField")RqRequestField rq) throws GException {
        return service.add(rq);
    }

    @DeleteMapping("/requestField/value")
    @ApiOperation("Удалить значения поля")
    public GResponse deleteValue(@RequestParam Long id) throws GNotFound {
        service.deleteValue(id);
        return new GResponse(GErrors.OK);
    }
    
    @DeleteMapping("/requestField")
    @ApiOperation("Удалить форму запроса по id")
    public GResponse delete(@RequestParam Long id) throws GException {
        service.delete(id);
        return new GResponse(GErrors.OK);
    }
}
