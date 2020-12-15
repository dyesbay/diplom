package app.expert.services;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.exceptions.GSystemError;
import app.base.services.GContextService;
import app.expert.constants.EventTypeValues;
import app.expert.constants.LogType;
import app.expert.db.event.Event;
import app.expert.db.event.EventCache;
import app.expert.db.event.EventRepository;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.request.Request;
import app.expert.db.request.RequestCache;
import app.expert.db.sessions.Session;
import app.expert.db.sessions.SessionRepository;
import app.expert.db.statics.event_types.EventType;
import app.expert.db.statics.event_types.EventTypeCache;
import app.expert.db.statics.letters.Letter;
import app.expert.db.statics.request_comments.RequestComment;
import app.expert.db.statics.status.Status;
import app.expert.models.RsEvent;
import app.expert.models.request.RqClose;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventCache eventCache;
    private final EventRepository eventRepository;
    private final ManagerCache managerCache;
    private final EventTypeCache eventTypeCache;
    private final RequestCache requestCache;
    private final GContextService context;
    private final SocketService socketService;
    private final SessionRepository sessionRepository;
    private final Logger logger = LoggerFactory.getLogger(EventService.class);

    private Event logEvent(Event event) {
        context.setLogType(LogType.EVENT_OZ.getValue());
        logger.info("EVENT : " + event.getType());
        context.setLogType(LogType.OTHER.getValue());
        context.setEventId(event.getId());
        context.setEventType(event.getType());
        return event;
    }

    private void checkInput(Event event) throws GNotFound, GNotAllowed {
        managerCache.find(event.getInitiator());
        eventTypeCache.find(event.getType());
    }

    private Date getNowInFormat() throws GSystemError {
        SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date now;
        try {
            now = sf.parse(sf.format(new Date()));
        } catch (ParseException e) {
            throw new GSystemError();
        }
        return now;
    }

    public RsEvent read(Long id) throws GNotAllowed, GNotFound {
        Event event = eventCache.find(id);
        EventType eventType = eventTypeCache.find(event.getType());
        return RsEvent.getFromEntity(event, eventType.getTemplate());
    }

    public Event add(Event event) throws GNotFound, GNotAllowed  {
        return eventCache.save(event);
    }

    public Event update(Long id, Event newEvent) throws GNotFound, GNotAllowed {
        Event event = eventCache.find(id);
        checkInput(newEvent);
        event.setBody(newEvent.getBody());
        event.setCommentary(newEvent.getCommentary());
        event.setCreated(newEvent.getCreated());
        event.setInitiator(newEvent.getInitiator());
        event.setRequest(newEvent.getRequest());
        event.setType(newEvent.getType());
        return eventCache.save(event);
    }
    
    public List<RsEvent> getByRequest(Long request) throws GNotAllowed, GNotFound {
        requestCache.find(request);
        List<Event> events = eventRepository.findByRequest(request, new Sort(Sort.Direction.DESC,"id"));
        List<RsEvent> rsEvents = new ArrayList<>(events.size());
        for (Event event : events) {
            EventType eventType = eventTypeCache.find(event.getType());
            event.setChecked(new Date());
            eventCache.save(event);
            rsEvents.add(RsEvent.getFromEntity(event, eventType.getTemplate()));
        }
        return rsEvents;
    }


    private void sendEvent(Request rq, Event event) {

        // посылаем событие назначенному сотруднику если он залогинен
        Session session = sessionRepository.findByManager(rq.getAssignee());
        if (session.getDisabled() == null && session.getExpired().compareTo(new Date()) >= 0) {
            socketService.sendToUser(session.getUserUuid().toString(), event.toString());
        }
    }

    private Event createEvent(EventTypeValues eventType, Long request, Map<String, Object> body) throws GNotAllowed, GNotFound, GSystemError {
        Request rq = requestCache.find(request);
        Manager initiator = managerCache.find(context.getUser());
        Event event = Event.builder()
                .created(getNowInFormat())
                .type(eventType.getKey())
                .initiator(initiator.getId())
                .request(request)
                .build();
        body.put("date", event.getCreated());
        body.put("manager", initiator.getFullName());
        body.put("request", request);
        event.setBody(body);
        event = eventCache.save(event);
        if (rq.getAssignee() != null) {
            Event finalEvent = event;
            CompletableFuture.runAsync(() -> sendEvent(rq, finalEvent));
        }
        return logEvent(event);
    }

    private Event createSystemEvent(EventTypeValues eventType, Long request, Map<String, Object> body) throws GNotAllowed, GNotFound, GSystemError {
        Request rq = requestCache.find(request);
        Event event = Event.builder()
                .created(getNowInFormat())
                .type(eventType.getKey())
                .request(request)
                .build();
        body.put("date", event.getCreated());
        event.setBody(body);
        return eventCache.save(event);
    }

    public Event createRequestCreatedEvent(Long request) throws GNotAllowed, GNotFound, GSystemError {
        Map<String, Object> body = new HashMap<>();
        Request rq = requestCache.find(request);
        body.put("text", rq.getText());
        body.put("channel", rq.getChannel());
        body.put("platform", rq.getPlatform().getValue());
        return createEvent(EventTypeValues.REQUEST_CREATED, request, body);
    }

    public Event createStatusChangeEvent(Long request, Status status) throws GNotAllowed, GNotFound, GSystemError {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.getCode());
        return createEvent(EventTypeValues.STATUS_CHANGED, request, body);
    }

    public Event createSystemStatusChangeEvent(Long request, Status status) throws GNotAllowed, GNotFound, GSystemError {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.getCode());
        return createSystemEvent(EventTypeValues.STATUS_CHANGED, request, body);
    }

    public Event createRouteChangeEvent(Long request) throws GNotAllowed, GNotFound, GSystemError {
        Map<String, Object> body = new HashMap<>();
        return createEvent(EventTypeValues.ROUTE_CHANGED, request, body);
    }

    public Event createEmailSentEvent(Long request, Letter letter) throws GNotAllowed, GNotFound, GSystemError {
        Map<String, Object> body = new HashMap<>();
        body.put("subject", letter.getSubject());
        body.put("body", letter.getBody());
        body.put("signature", letter.getSignature());
        return createEvent(EventTypeValues.EMAIL_SENT, request, body);
    }

    public Event createCommentEvent(Long request, RequestComment comment) throws GNotAllowed, GNotFound, GSystemError {
        Map<String, Object> body = new HashMap<>();
        body.put("comment", comment.getComment());
        return createEvent(EventTypeValues.COMMENT_ADDED, request, body);
    }

    public Event createCloseRequestEvent(Long request, RqClose rq, Status status) throws GNotAllowed, GNotFound, GSystemError {
        Map<String, Object> body = new HashMap<>();
        body.put("comment", rq.getComment() == null ? null : rq.getComment());
        body.put("reason", status.getName());
        return createEvent(EventTypeValues.REQUEST_CLOSED, request, body);
    }
}
