package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.models.GResponse;
import app.expert.constants.AccessType;
import app.expert.constants.Platform;
import app.expert.models.subject.RqSubject;
import app.expert.models.subject.RsSubject;
import app.expert.services.SubjectService;
import app.expert.services.SubjectsAccessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/static")
@Api(tags = "1003. Темы обращений")
public class Subjects extends GControllerAdvice {
      
    private final SubjectService service;
    private final SubjectsAccessService subjectsAccessService;

    @GetMapping("/subject")
    @ApiOperation("Получить тему по id")
    public RsSubject get(@RequestParam Long subject, @RequestParam(required = false) Long subSubjects) throws GException {
        if(subSubjects == null)
            return service.get(subject);
        return service.get(subject, subSubjects);
    }

    @GetMapping("/subjects")
    @ApiOperation("Получить список тем обращения с подтематиками")
    public List<RsSubject> getAll() throws GException {
        return service.getAll();
    }
    
    @PutMapping("/subject")
    @ApiOperation("Добавить тему")
    public RsSubject add(@Validated @ModelAttribute("rqSubject") RqSubject rq) throws GException {
        return service.add(rq);
    }
    
    @DeleteMapping("/subject")
    @ApiOperation("Удалить тему")
    public GResponse delete(@RequestParam Long id) throws GException {
        service.delete(id);
        return new GResponse(GErrors.OK);
    }
    
    @PatchMapping("/subject")
    @ApiOperation("Изменить тему")
    public RsSubject update(@RequestParam Long id,@Validated @ModelAttribute("rqSubject") RqSubject rq) throws GException {
        return service.update(id,rq);
    }

    @PutMapping("/subject/editAccess")
    @ApiOperation("Поменять уровень доступа к тематике")
    public GResponse editSubjectAccess(@RequestParam Long subject,
                                       @RequestParam AccessType accessType,
                                       @RequestParam Platform platform) throws GNotAllowed, GNotFound {
        return subjectsAccessService.editSubjectAccess(subject, platform, accessType);
    }
}
