package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.db.admission.Admission;
import app.expert.db.admission.AdmissionCache;
import app.expert.db.admission.AdmissionRepository;
import app.expert.db.call.Call;
import app.expert.db.call.CallCache;
import app.expert.db.client.Client;
import app.expert.db.client.ClientCache;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.statics.region.Region;
import app.expert.db.statics.region.RegionsCache;
import app.expert.db.statics.subjects.Subject;
import app.expert.db.statics.subjects.SubjectCache;
import app.expert.models.admission.RqAdmission;
import app.expert.models.admission.RsAdmission;
import app.expert.validation.GPhoneParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdmissionService {

    private final AdmissionCache cache;
    private final AdmissionRepository repo;
    private final AdmissionEventService admEventService;
    private final ClientCache clientCache;
    private final CallCache callCache;
    private final ManagerCache managerCache;
    private final RegionsCache regionCache;
    private final RegionsService regionsService;
    private final SubjectCache subjectCache;

    /**
     * Получение данных об обращении по идентификатору звонка или по id обращения
     * @param callId - индентификатор звонка, общий с телефонией.
     */
    public RsAdmission getByCallIdOrId(Long id, String callId) throws GNotAllowed, GNotFound, GBadRequest {
        Call call;
        Admission adm;
        if(id != null) {
            adm = cache.find(id);
            call = callCache.find(adm.getCall());
        }
        else if(callId != null) {
            call = callCache.getByCallId(callId);
            adm = cache.findByCall(call.getId());
        }
        else throw new GBadRequest();

        Client client = clientCache.getByPhone(call.getPhone());
        Manager manager = managerCache.findByAgentId(call.getAgentId());
        Subject subject = null;
        Region region = null;
        if(adm.getSubject() != null)
            subject = subjectCache.find(adm.getSubject());
        if(client.getRegion() != null)
            region = regionCache.find(client.getRegion());

        return RsAdmission.get(adm, manager, region, call, admEventService.getAdmissionEvents(call.getIdentifier()),subject);
    }

    /**
     * Регистрация обращения.
     * Если клиент не найден в базе по номеру телефона,
     * создаеться новый клиент с телефоном и регионом.
     * id клиента записывается в текущее обращение.
     * При этом у оператора остаеться возможность редактировать данные клиента.
     * @param callIdentifier - индентификатор звонка,
     *                       общий с телефонией.
     */
    public Admission registerAdmission(String callIdentifier) throws GException {
        Call call = callCache.getByCallId(callIdentifier);
        Client client = clientCache.getByPhone(call.getPhone());
        if(client == null) {
            client = new Client();
            client.setPhone(call.getPhone());
            try {
                client.setRegion(regionsService.getRegionByPhone(call.getPhone()).getId());
            } catch (GException e) {
                /*если не удалось определить регион по номеру телефона*/
                client.setRegion(null);
            }
        }
        String fullName = client.getFullName().trim();
        if(fullName.isEmpty())
            fullName = null;

        client = clientCache.save(client);
        Admission adm = Admission.builder()
                .call(call.getId())
                .client(client.getId())
                .clientName(fullName)
                .created(call.getStarted())
                .clientPhone(client.getPhone())
                .clientEmail(client.getEmail())
                .build();
        return cache.save(adm);
    }

    /**
     * Завершение обращения. При завершении добавляються данные звонка,
     * обновляються данные по клиенту
     * (например если оператор внес клиента в базу во время обращения).
     * @param callId - индентификатор звонка, общий с телефонией.
     */
    public Admission completeAdmission(String callId) throws GNotFound {
        Call call = callCache.getByCallId(callId);
        Admission adm = repo.findByCall(call.getId()).orElseThrow(() -> new GNotFound(cache.getNotFoundError()));
        Client client = clientCache.getByPhone(call.getPhone());
        adm.setClientName(client.getFullName());
        adm.setClientEmail(client.getEmail());
        adm.setClientPhone(client.getPhone());
        return cache.save(adm);
    }

    /**
     * Окончательная обработка обращения - добавление темы и результата звонка.
     * Если дополнительные данные не были переданы ,
     * обращение сохраняеться остаеться в текущем состоянии.
     * Некоторые поля допускают значение null
     * При этом данные клиента: e-mail, номер телефона и имя могут быть изменены.
     * @param id - id обращения в базе
     * @param rq - Модель данных, для изменения данных обращения.
     */

    public RsAdmission update(Long id, String callIdentifier, RqAdmission rq) throws GNotAllowed, GNotFound, GBadRequest {
        RsAdmission rs = getByCallIdOrId(id, callIdentifier);
        Admission adm = cache.find(rs.getId());
        Call call = callCache.find(adm.getCall());
        Manager manager = managerCache.findByAgentId(call.getAgentId());
        Client client = clientCache.find(adm.getClient());
        if (rq.getClientName() != null ) adm.setClientName(rq.getClientName());
        if (rq.getEmail() != null) adm.setClientEmail(rq.getEmail());
        if (rq.getPhone() != null) adm.setClientPhone(GPhoneParser.parsePhone(rq.getPhone()));
        if (rq.getResult() != null) adm.setResult(rq.getResult());
        if (rq.getSubject() != null) {
            subjectCache.find(rq.getSubject());
            adm.setSubject(rq.getSubject());
        }
        Subject subject = null;
        Region region = null;
        if(adm.getSubject() != null)
            subject = subjectCache.find(adm.getSubject());
        if(client.getRegion() != null)
            region = regionCache.find(client.getRegion());
        return  RsAdmission.get(cache.save(adm), manager, region, call, admEventService.getAdmissionEvents(call.getIdentifier()),subject);
    }

    /**
     * Добавить комментарий к обращению
     */
    public RsAdmission addComment(Long id, String callId, String comment) throws GNotAllowed, GNotFound, GBadRequest {
        RsAdmission rsAdm = getByCallIdOrId(id, callId);
        Admission adm = cache.find(rsAdm.getId());
        adm.setComment(comment);
        cache.save(adm);
        admEventService.addCommentEvent(rsAdm.getId(),comment);
        return getByCallIdOrId(id, callId);
    }
}
