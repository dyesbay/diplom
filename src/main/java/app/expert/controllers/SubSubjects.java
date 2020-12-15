package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.models.sub_subject.RqSubSubject;
import app.expert.models.sub_subject.RsSubSubject;
import app.expert.services.SubSubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/static")
@RequiredArgsConstructor
@Api(tags = "1003. Подтематики")
public class SubSubjects extends GControllerAdvice {

    private final SubSubjectService service;

    @GetMapping("/subSubject")
    @ApiOperation("Получить подтематику по id")
    public RsSubSubject get(@RequestParam Long id) throws GException {
        return service.get(id);
    }

    @PutMapping("/subSubject")
    @ApiOperation("Добавить подтематику")
    public RsSubSubject add(@ModelAttribute("rq")RqSubSubject rq) throws GException {
        return service.add(rq);
    }

    @DeleteMapping("/subSubject")
    @ApiOperation("Удалить подтематику")
    public GResponse delete(@RequestParam Long id) throws GException {
        service.delete(id);
        return new GResponse(GErrors.OK);
    }
}
