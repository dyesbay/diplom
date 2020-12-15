package app.expert.services;

import app.base.exceptions.*;
import app.expert.constants.ExpertErrors;
import app.expert.db.event.Event;
import app.expert.db.event.EventRepository;
import app.expert.db.request.Request;
import app.expert.db.request.RequestCache;
import app.expert.db.statics.status.Status;
import app.expert.db.statics.status.StatusCache;
import app.expert.models.request.RqClose;
import app.expert.models.request.RsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CloseRequestService {

    private final RequestCache rqCache;
    private final StatusCache statusCache;
    private final EventService eventService;
    private final EventRepository eventRepository;

    public RsRequest closeRequest (RqClose rq) throws GNotAllowed, GNotFound, GSystemError, GBadRequest {
        check(rq);
        Request request = rqCache.find(rq.getId());
        Status currentStatus = statusCache.find(request.getStatus());
        String newStatus = null;
        for(String status : rq.getReason().getValues()) {
            if(statusCache.find(status).getRouteType().equals(currentStatus.getRouteType())){
                    newStatus = status;
            }
        }
        //Если ОЗ закрываеться по причине дублирования
        if(rq.getReason().getValues().contains("0"))
            newStatus = "0";

        // Если статус не был присвоен переменной newStatus, значит
        // CloseReason был выбран не верно
        if(newStatus == null) {
            throw new GBadRequest(ExpertErrors.WRONG_CLOSE_STATUS);
        }
        request.setStatus(newStatus);
        Event event = eventService.createCloseRequestEvent(request.getId(), rq, statusCache.find(request.getStatus()));
        request.setClosed(event.getCreated());
        rqCache.save(request);
        RsRequest rsRequest = RsRequest.get(request);
        rsRequest.setEvents(eventService.getByRequest(request.getId()));
        return rsRequest;
    }

    private void check(RqClose rq) throws GBadRequest, GNotAllowed, GNotFound {
        if(rqCache.find(rq.getId()).getClosed() != null)
            throw new GBadRequest(ExpertErrors.REQUEST_ALREADY_CLOSED);
    }
}
