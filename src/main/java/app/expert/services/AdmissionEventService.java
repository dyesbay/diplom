package app.expert.services;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.services.GContextService;
import app.base.utils.DateUtils;
import app.expert.constants.AdmissionEventTypes;
import app.expert.constants.ExpertErrors;
import app.expert.constants.LogType;
import app.expert.db.admission.Admission;
import app.expert.db.admission.AdmissionCache;
import app.expert.db.admission_event.AdmissionEvent;
import app.expert.db.admission_event.AdmissionEventCache;
import app.expert.db.call.Call;
import app.expert.db.call.CallCache;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.request.Request;
import app.expert.db.statics.subjects.SubjectCache;
import app.expert.models.admission.RsAdmissionEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdmissionEventService {

    private final AdmissionEventCache cache;
    private final AdmissionCache admissionCache;
    private final CallCache callCache;
    private final ManagerCache managerCache;
    private final SubjectCache subjectCache;
    private final AdmissionEventTypeService admEventTypeServ;
    private final GContextService context;
    private final Logger logger = LoggerFactory.getLogger(AdmissionEventService.class);

    private void logEvent(AdmissionEvent event) {
        context.setLogType(LogType.EVENT_ADMISSION.getValue());
        logger.info("EVENT : " + event.getType().getValue());
        context.setLogType(LogType.OTHER.getValue());
    }

    /**
     * Регистрирует события начала, завршения звонка.
     *
     * @param adm  - обращение к которому должно относиться событие
     * @param type - Тип события @link{AdmissionEventTypes.class}
     * @throws GNotFound - если не найден звонок к которому относиться обращение
     */
    public void registerCallEvent(Admission adm, AdmissionEventTypes type) throws GNotFound, GNotAllowed, ParseException {
        Call call = callCache.find(adm.getCall());
        Map<String, Object> body = new HashMap<>();
        Manager initiator;
        try {
            initiator = managerCache.find(call.getManager());
        } catch (GNotFound e){
            initiator = managerCache.find(context.getUser());
        }
        body.put("phone" , call.getPhone());
        body.put("manager", initiator.getFullName());
        AdmissionEvent event = AdmissionEvent.builder()
                .admission(adm.getId())
                .initiator(call.getManager())
                .body(body)
                .build();
        if(type.equals(AdmissionEventTypes.START_CALL)) {
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_DATE_TIME);
            formatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            event.setCreated(formatter.parse(formatter.format((call.getStarted()))));
            body.put("date" , formatter.format(call.getStarted()));
            event.setType(AdmissionEventTypes.START_CALL);
        } else if(type.equals(AdmissionEventTypes.COMPLETE_CALL)) {
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_DATE_TIME);
            formatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            event.setCreated(formatter.parse(formatter.format((call.getEnded()))));
            body.put("date" , formatter.format(call.getEnded()));
            event.setType(AdmissionEventTypes.COMPLETE_CALL);
        } else {
            throw new GNotAllowed(ExpertErrors.WRONG_ADMISSION_EVENT_TYPE);
        }
        logEvent(cache.save(event));
    }

    /**
     * Регестрирует события создания отложенного запроса.
     * @param adm - Обращение к которому относиться событие
     * @param type - Тип события @link{AdmissionEventTypes.class}
     * @param deferred - отложенный запрос,
     *                 создание которого нужно отразить в истории обращения
     * @throws GNotFound - если не найден звонок или менеджер к которому относиться обращение
     */
    public void registerDefRequestEvent (Admission adm, AdmissionEventTypes type, Request deferred) throws GNotAllowed, GNotFound {
        Call call = callCache.find(adm.getCall());
        Map<String, Object> body = new HashMap<>();
        Manager initiator;
        try {
            initiator = managerCache.find(call.getManager());
        } catch (GNotFound e){
            initiator = managerCache.find(context.getUser());
        }
        body.put("manager", initiator.getFullName());
        if (deferred.getChannel() != null)
            body.put("channel", deferred.getChannel());
        if(deferred.getText() != null)
            body.put("request_text", deferred.getText());
        if(deferred.getSubject() != null) {
            body.put("subject", subjectCache.find(deferred.getSubject()).getName());
            if(deferred.getSubSubject() != null && deferred.getSubSubject() != 0) {
                body.put("subSubject", subjectCache.find(deferred.getSubSubject()).getName());
            }
        }

        AdmissionEvent event = AdmissionEvent.builder()
                .type(type)
                .created(deferred.getCreated())
                .initiator(initiator.getId())
                .admission(adm.getId())
                .body(body)
                .build();
        logEvent(cache.save(event));
    }

    /**
     * Регистрация события - добавлен комментарий к обращению
     */
    public void addCommentEvent(Long id, String comment) throws GNotAllowed, GNotFound {
        Admission adm = admissionCache.find(id);
        Map<String, Object> body = new HashMap<>();
        Manager initiator = managerCache.find(context.getUser());
        Date date = new Date();
        body.put("date", date);
        body.put("manager", initiator.getFullName());
        body.put("comment", comment);
        AdmissionEvent event = AdmissionEvent.builder()
                .type(AdmissionEventTypes.COMMENT_ADDED)
                .created(date)
                .initiator(initiator.getId())
                .admission(adm.getId())
                .body(body)
                .build();
        logEvent(cache.save(event));
    }

    /**
     * Поиск всех событий для обращения
     * @param identifier - идентификатор звонка
     */
    public List<RsAdmissionEvent> getAdmissionEvents(String identifier) throws GNotFound, GNotAllowed {
        Call call = callCache.getByCallId(identifier);
        Admission adm = admissionCache.findByCall(call.getId());
        Manager manager = managerCache.find(call.getManager());
        return cache.findByAdmission(new Sort(Sort.Direction.DESC, "id"), adm.getId())
                .stream()
                .map((s) -> RsAdmissionEvent.get(s, admEventTypeServ, manager))
                .collect(Collectors.toList());
    }
}
