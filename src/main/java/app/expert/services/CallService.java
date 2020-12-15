package app.expert.services;

import app.base.exceptions.GAlreadyExists;
import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.utils.DateUtils;
import app.expert.db.admission.Admission;
import app.expert.db.call.Call;
import app.expert.db.call.CallCache;
import app.expert.db.call.CallRepository;
import app.expert.db.manager.ManagerCache;
import app.expert.constants.AdmissionEventTypes;
import app.expert.models.call.CallType;
import app.expert.models.call.RqCompCall;
import app.expert.models.call.RqRegCall;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class CallService {

    private final CallCache cache;
    private final CallRepository repo;
    private final ManagerCache managerCache;
    private final AdmissionService admService;
    private final AdmissionEventService admEventService;

    public Call get(Long id) throws GNotAllowed, GNotFound {
        return cache.find(id);
    }

    /**
     * Регистрация звонка.
     * Записывает данные в базу, полученные от телефонии при начале звонка.
     * Автоматически создает обращение.
     * @param rq - модель данных входящих запросов для звонков
     */
    public Call registerCall(RqRegCall rq) throws GException, ParseException {
        checkBoundItems(rq);
        if(rq.getStarted() == null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_DATE_TIME);
            rq.setStarted(formatter.parse(formatter.format(new Date())));
        }
        if(rq.getCallType() == null)
            rq.setCallType(CallType.INCOMING);
        if(rq.getStationId() == null)
            rq.setStationId(managerCache.findByAgentId(rq.getAgentId()).getStationId());

        Call call = cache.save(rq.getCall(managerCache));
        Admission adm = admService.registerAdmission(rq.getIdentifier());
        admEventService.registerCallEvent(adm, AdmissionEventTypes.START_CALL);
        return call;
    }

    /**
     * Завершения звонка.
     * Окончательные данные onHold(время на удержании)
     * и время завершения звонка(endedOn) записываються в базу.
     * Автоматически завершает обращение обращение.
     */
    public Call completeCall(RqCompCall rq) throws GException, ParseException {
        Call call = repo.findByIdentifier(rq.getIdentifier())
                .orElseThrow(() -> new GNotFound(cache.getNotFoundError()));
        if(rq.getEnded() == null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_DATE_TIME);
            rq.setEnded(formatter.parse(formatter.format(new Date())));
        }
        if(rq.getOnHold() == null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_TIME);
            rq.setOnHold(new Date(0));
        }
        if (call.getEnded() != null)
            throw new GAlreadyExists(cache.getAlreadyComplete());
        call.setOnHold(rq.getOnHold());
        call.setEnded(rq.getEnded());
        cache.save(call);
        Admission adm = admService.completeAdmission(rq.getIdentifier());
        admEventService.registerCallEvent(adm, AdmissionEventTypes.COMPLETE_CALL);
        return getByIdentifier(call.getIdentifier());
    }

    private void checkBoundItems(RqRegCall rq) throws GAlreadyExists,
            GNotFound, GNotAllowed {
        managerCache.findByAgentId(rq.getAgentId());
        if (repo.findByIdentifier(rq.getIdentifier()).isPresent())
            throw new GAlreadyExists(cache.getAlreadyExistsError());
    }

    public Call getByIdentifier(String identifier) throws GNotFound {
        return repo.findByIdentifier(identifier)
                .orElseThrow(() -> new GNotFound(cache.getNotFoundError()));
    }
}
