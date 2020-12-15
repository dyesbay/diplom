package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.exceptions.GSystemError;
import app.base.services.GContextService;
import app.base.utils.DateUtils;
import app.expert.constants.DayTypes;
import app.expert.db.request.Request;
import app.expert.db.request.RequestCache;
import app.expert.db.schedule.general_schedule.GeneralSchedule;
import app.expert.db.schedule.general_schedule.GeneralScheduleRepository;
import app.expert.db.statics.status.Status;
import app.expert.db.statics.status.StatusCache;
import app.expert.models.manager.RSManager;
import app.expert.models.request.RsRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class RequestStatusChangeService {


    private static final Map<String, List<String>> statusChanges = new HashMap<>();
    private final GContextService context;
    private final ManagerService managerService;
    private final StatusService statusService;
    private final RequestCache requestCache;
    private final EventService eventService;
    private final GeneralScheduleRepository generalScheduleRepository;
    private final StatusCache statusCache;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public RequestStatusChangeService(GContextService context,
                                      ManagerService managerService,
                                      StatusService statusService,
                                      RequestCache requestCache,
                                      EventService eventService, GeneralScheduleRepository generalScheduleRepository,
                                      StatusCache statusCache) {
        this.context = context;
        this.managerService = managerService;
        this.statusService = statusService;
        this.requestCache = requestCache;
        this.eventService = eventService;
        this.generalScheduleRepository = generalScheduleRepository;
        this.statusCache = statusCache;
        List<String> pendingChanges = new ArrayList<>();
        pendingChanges.add("IN_PROCESSING");
        pendingChanges.add("EXPIRED");
        pendingChanges.add("EXTERNAL_SERVICE");

        List<String> inProcessingChanges = new ArrayList<>();
        inProcessingChanges.add("IN_PROCESSING");
        inProcessingChanges.add("CLOSED");
        inProcessingChanges.add("EXPIRED");
        inProcessingChanges.add("EXTERNAL_SERVICE");

        List<String> closedChanges = new ArrayList<>();
        closedChanges.add("EXPIRED");

        List<String> externalServiceChanges = new ArrayList<>();
        externalServiceChanges.add("IN_PROCESSING");
        externalServiceChanges.add("EXTERNAL_SERVICE");
        externalServiceChanges.add("CLOSED");
        externalServiceChanges.add("EXPIRED");

        statusChanges.put("PENDING", pendingChanges);
        statusChanges.put("IN_PROCESSING", inProcessingChanges);
        statusChanges.put("CLOSED", closedChanges);
        statusChanges.put("EXTERNAL_SERVICE", externalServiceChanges);
        statusChanges.put("EXPIRED", new ArrayList<>());
    }


    private Date increment(Date date) {
        return DateUtils.convert(new Date(date.getTime() + 24 * 60 * 60 * 1000L), DateUtils.HUMAN_DATE);
    }


    private Date searchDateInRepo(Long numOfBusinessDays) {
        Date dt = new Date();
        for (int i = 0; i <= numOfBusinessDays; i++) {
            dt = increment(dt);
        }
        GeneralSchedule gs = generalScheduleRepository.findByDate(dt);
        if (gs != null && gs.getType().equals(DayTypes.WORK_DAY.getKey())) {
            return gs.getDate();
        } else if (gs != null) {
            while (true) {
                dt = increment(dt);
                gs = generalScheduleRepository.findByDate(dt);
                if (gs != null && gs.getType().equals(DayTypes.WORK_DAY.getKey()))
                    return gs.getDate();
                else if (gs == null) break;
            }
        }
        return null;
    }

    public void setLifespans(Request request) throws GNotAllowed, GNotFound {

        Status status = statusCache.find(request.getStatus());
        if (status.getExpiredDaysNum() > 0) {
            Long numOfBusinessDays = status.getExpiredDaysNum();
            // получаем дату завтрашнего дня и к ней прибавляем количество дней из расписания
            request.setExpiresDate(searchDateInRepo(numOfBusinessDays));
        }
        if (status.getLifeSpan() > 0) {
            Long numOfBusinessDays = status.getLifeSpan();
            request.setLifeSpanDate(searchDateInRepo(numOfBusinessDays));
        }
    }


    public RsRequest changeStatus(Request request, String status) throws GSystemError, GNotAllowed, GBadRequest, GNotFound {
        Status statusEntity = statusService.get(status);

        String requestStatusState = statusService.get(request.getStatus()).getState().getKey();
        String oldStatus = request.getStatus();
        if (statusChanges.get(requestStatusState).contains(statusEntity.getState().getKey())) {
            request.setStatus(status);
        } else {
            throw new GBadRequest();
        }
        setLifespans(request);
        request = requestCache.save(request);
        if (oldStatus.charAt(0) != status.charAt(0))
            eventService.createRouteChangeEvent(request.getId());

        eventService.createStatusChangeEvent(request.getId(), statusEntity);

        return RsRequest.get(request);
    }

    public RsRequest changeStatusSystem(Request request, String status) throws GSystemError, GNotAllowed, GBadRequest, GNotFound {
        Status statusEntity = statusService.get(status);

        String requestStatusState = statusService.get(request.getStatus()).getState().getKey();
        String oldStatus = request.getStatus();
        if (statusChanges.get(requestStatusState).contains(statusEntity.getState().getKey())) {
            request.setStatus(status);
        } else {
            throw new GBadRequest();
        }
        setLifespans(request);
        request = requestCache.save(request);
        if (oldStatus.charAt(0) != status.charAt(0))
            eventService.createRouteChangeEvent(request.getId());

        eventService.createSystemStatusChangeEvent(request.getId(), statusEntity);

        return RsRequest.get(request);
    }

    public RsRequest changeStatus(Long request, String status) throws GNotAllowed, GBadRequest, GNotFound, GSystemError {

        RSManager manager = managerService.get(context.getUser());
        Request requestEntity = requestCache.find(request);
        if (!(manager.getRole().getCode().equals("oooz_head") ||
                (requestEntity.getAssignee() != null && requestEntity.getAssignee().equals(context.getUser())))) {
            throw new GNotAllowed();
        }
        // проверка на сущетсвование статуса
        return changeStatus(requestEntity, status);
    }
}
