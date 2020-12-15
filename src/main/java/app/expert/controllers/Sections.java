package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.models.section.RqSection;
import app.expert.models.section.RsSection;
import app.expert.services.SectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/static")
@Api(tags = "1002. Разделы")
@RequiredArgsConstructor
public class Sections extends GControllerAdvice {

    private final SectionService service;

    @GetMapping("/section")
    @ApiOperation("Запросить раздел по id")
    public RsSection get(@RequestParam Long id) throws GException {
        return service.get(id);
    }

    @PutMapping("/section")
    @ApiOperation("Добавить новый раздел")
    public RsSection add(@ModelAttribute(name = "rqSection") RqSection rq) throws GException {
        return service.add(rq);
    }

    @PatchMapping("/section")
    @ApiOperation("Изменение существующего раздела")
    public RsSection update(@RequestParam Long id, @ModelAttribute(name = "rqSection") RqSection rq) throws GException {
        return service.update(id,rq);
    }

    @DeleteMapping("/section")
    @ApiOperation("Удалить раздел")
    public GResponse delete(@RequestParam Long id) throws GException {
        service.delete(id);
        return new GResponse(GErrors.OK);
    }

    @GetMapping("/sections")
    @ApiOperation("Запросить все разделы")
    public List<RsSection> getAll() throws GException {
        return service.getAll();
    }
}
