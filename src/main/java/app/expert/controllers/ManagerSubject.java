package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.models.manager.RSManager;
import app.expert.models.manager.RsManagerSubject;
import app.expert.models.subject.RsSubject;
import app.expert.services.ManagerSubjectService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/static")
@Api(tags = "1012. Тематики сотрудников")
public class ManagerSubject extends GControllerAdvice {

    private final ManagerSubjectService service;

    @PutMapping("/managerSubjects")
    public List<RsManagerSubject> setSubjects(@RequestParam Long manager, @RequestParam List<Long> subjects)
            throws GException {

        return service.setSubjects(manager, subjects);
    }

    @PatchMapping("/managerSubjects/exclude")
    public RsSubject excludeManagersForSubject(@RequestParam Long subject, @RequestParam List<Long> managers)
            throws GNotAllowed, GNotFound {

        return service.excludeManagersForSubject(subject, managers);
    }

    @GetMapping("/managerSubjects/excluded")
    public List<RSManager> getExcludedManagers(@RequestParam Long subject) throws GNotAllowed, GNotFound {

        return service.getExcludedManagers(subject);
    }
}
