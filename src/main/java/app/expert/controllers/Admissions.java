package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.models.admission.RqAdmission;
import app.expert.models.admission.RsAdmission;
import app.expert.services.AdmissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Api(tags = "1031. Обращения")
public class Admissions extends GControllerAdvice {
    
    private final AdmissionService service;

    @GetMapping("/admission")
    @ApiOperation("Получить карточку обращения по идентификатору звонка или id обращения")
    public RsAdmission get(@RequestParam(required = false) Long id, @RequestParam(required = false) String callId)
            throws GNotAllowed, GNotFound, GBadRequest {
        return service.getByCallIdOrId(id, callId);
    }

    @PatchMapping("/admission")
    @ApiOperation("Изменить карточку обращения")
    public RsAdmission update(@RequestParam(required = false) Long id,
                              @RequestParam(required = false) String callId,
                              @ModelAttribute("rqAdmission") RqAdmission rq) throws GNotAllowed, GNotFound, GBadRequest {
        return service.update(id, callId, rq);
    }

    @PutMapping("/admission/comment")
    @ApiOperation("Добавить комментарий к обращению")
    public RsAdmission update(@RequestParam(required = false) Long id,
                              @RequestParam(required = false) String callId,
                              @RequestParam (required = false) String comment) throws GNotAllowed, GNotFound, GBadRequest {
        return service.addComment(id, callId, comment);
    }
}
