package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.*;
import app.base.models.GResponse;
import app.expert.constants.Channel;
import app.expert.db.statics.letters.Letter;
import app.expert.models.RqReassign;
import app.expert.models.letter.RqLetter;
import app.expert.models.request.*;
import app.expert.services.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;


@RestController
@ControllerAdvice
@Api(tags = "1021. Запросы")
@RequiredArgsConstructor
public class Requests extends GControllerAdvice {

    private final RequestService service;
    private final DistributeRequestsService distributeRequestsService;
    private final RequestStatusChangeService requestStatusChangeService;
    private final MailSenderService mailSenderService;
    private final CloseRequestService closeRequestService;

    @GetMapping("/request")
    @ApiOperation("Получить запрос по id")
    public RsRequest get(@RequestParam Long id) throws GException {
        return service.get(id);
    }

    @PutMapping("/request")
    @ApiOperation("Добавить запрос")
    public RsRequest add(@Validated @RequestBody RqRequest rq) throws GException {
        return service.add(rq);
    }

    @PatchMapping("/request")
    @ApiOperation("Редактировать запрос")
    public RsRequest edit(@Validated @RequestBody RqEditRequest rq) throws GException {
        return service.edit(rq);
    }

    @DeleteMapping("/request")
    @ApiOperation("Удалить запрос")
    public GResponse delete(@RequestParam Long id) throws GException {
        service.delete(id);
        return new GResponse(GErrors.OK);
    }

    @PutMapping("/requests/distribute")
    @ApiOperation("Распределить отложенные запросы среди сотрудников")
    public List<RsRequest> distribute(@RequestParam List<String> managers)
            throws GNotFound, GNotAllowed, GBadRequest, GSystemError {

        return distributeRequestsService.distribute(managers, false);
    }

    @PutMapping("/requests/assign")
    @ApiOperation("Вручную назначить сотрудников")
    public List<RsRequest> assign(@RequestParam List<String> managers,
                                  @RequestParam List<Long> requests,
                                  @RequestParam(required = false) List<Channel> channel,
                                  @RequestParam(required = false) Long totalRequestsNumber,
                                  @RequestParam(required = false) Long requestsNumberPerManager)
            throws GNotFound, GNotAllowed, GSystemError, GBadRequest {

        return distributeRequestsService.assigneeManagersToRequests(managers, requests, channel, totalRequestsNumber, requestsNumberPerManager);
    }


    @PutMapping("/requests/reassign")
    @ApiOperation("Переназначит запросы у отсутствующего сотрудника")
    public List<RsRequest> reassign(@RequestBody RqReassign rq)
            throws GNotFound, GNotAllowed, GSystemError, GBadRequest {

        return distributeRequestsService.reassignManagersToRequests(rq);
    }

    @PatchMapping("/request/changeStatus")
    @ApiOperation("Изменить статус запроса")
    public RsRequest changeStatus(@RequestParam Long request, @RequestParam String status)
            throws GNotAllowed, GBadRequest, GNotFound, GSystemError {
        return requestStatusChangeService.changeStatus(request, status);
    }

    @PutMapping("/request/comment")
    @ApiOperation("Добавить комментарий к запросу")
    public RsRequestComment addComment(@RequestParam Long request, @RequestParam String text)
            throws GNotAllowed, GNotFound, GSystemError {
        return service.addComment(request, text);
    }

    @GetMapping("/request/allComments")
    @ApiOperation("Запросить все комментарии к запросу")
    public List<RsRequestComment> getAll(@RequestParam Long request) {
        return service.getAllByRequest(request);
    }

    @PostMapping("/request/send_mail")
    @ApiOperation("Отправить письмо клиенту")
    public Letter send(@Validated @ModelAttribute("mail") RqLetter rq) throws GNotAllowed,
            GNotFound,
            IOException,
            MessagingException,
            GBadRequest,
            GSystemError {
        return mailSenderService.saveAndSendEmail(rq);
    }

    @PatchMapping("/request/close")
    @ApiOperation("Закрыть запрос")
    public RsRequest close(@Validated @ModelAttribute("close") RqClose rq) throws GNotAllowed,
            GNotFound,
            GSystemError, GAlreadyExists, GBadRequest {
        return closeRequestService.closeRequest(rq);
    }
}
