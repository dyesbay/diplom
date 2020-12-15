package app.expert.services;

import app.base.exceptions.*;
import app.base.services.GContextService;
import app.expert.constants.AdmissionEventTypes;
import app.expert.constants.ConfigCode;
import app.expert.constants.Platform;
import app.expert.db.admission.AdmissionCache;
import app.expert.db.client.Client;
import app.expert.db.client.ClientCache;
import app.expert.db.configs.ConfigCache;
import app.expert.db.request.ApplicantContacts;
import app.expert.db.request.Request;
import app.expert.db.request.RequestCache;
import app.expert.db.request.RequestRepository;
import app.expert.db.statics.request_comments.RequestComment;
import app.expert.db.statics.request_comments.RequestCommentsCache;
import app.expert.db.statics.request_comments.RequestCommentsRepository;
import app.expert.db.statics.status.Status;
import app.expert.db.statics.status.StatusCache;
import app.expert.models.RsEvent;
import app.expert.models.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RequestService {

    private final RequestCache cache;
    private final ClientCache clientCache;
    private final RequestRepository repo;
    private final RequestFieldSubjectService requestFieldSubjectService;

    private final RequestCache requestCache;
    private final AdmissionService admissionService;
    private final AdmissionEventService admissionEventService;
    private final AdmissionCache admissionCache;
    private final EventService eventService;
    private final GContextService context;
    private final RequestCommentsRepository requestCommentsRepository;
    private final RequestCommentsCache requestCommentsCache;
    private final ConfigCache configCache;
    private final RequestStatusChangeService requestStatusChangeService;
    private final ClientService clientService;
    private final StatusCache statusCache;

    private final EntityManager em;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * метод для распределения запросов по платформам
     *
     * @param request - запрос
     * @throws GNotAllowed - если конфига для распределения нет
     * @throws GNotFound   - если конфига для распределения нет
     */
    private void setPlatform(Request request) throws GNotAllowed, GNotFound {
        // получить процент для КАЗАНИ

        double kazan = Double.parseDouble(configCache.find(ConfigCode.REQUEST_DISTRIBUTION.getKey()).getValue());

        // получить список всех запросов в работе и понять какое текущее соотношение
        TypedQuery<Request> query = em.createQuery(
                "SELECT r FROM Request r where r.status in (select code from Status where state = 'IN_PROCESSING')", Request.class);
        List<Request> allRequests = query.getResultList();
        List<Request> kazanRequests = allRequests.stream()
                .filter(entity -> entity.getPlatform().getKey().equals(Platform.KAZAN.getKey())).collect(Collectors.toList());

        // в соответсвии с этим установить платформу
        if ((double) kazanRequests.size() / allRequests.size() <= kazan)
            request.setPlatform(Platform.KAZAN);
        else
            request.setPlatform(Platform.KURSK);
    }

    private Map<String, Object> getClientContactsMap(RqRequest request, Client client) {
        return getStringObjectMap(client, request.getClientContacts());
    }

    private Map<String, Object> getClientContactsMap(RqEditRequest request, Client client) {
        return getStringObjectMap(client, request.getClientContacts());
    }

    private Map<String, Object> getStringObjectMap(Client client, ApplicantContacts applicantContacts) {
        Map<String, Object> clientContacts = new HashMap<>();
        if (applicantContacts.getCommType() == null)
            applicantContacts.setCommType(client.getCommunicationType());
        if (applicantContacts.getPhone() == null)
            applicantContacts.setPhone(client.getPhone());
        if (applicantContacts.getEmail() == null)
            applicantContacts.setEmail(client.getEmail());
        if (applicantContacts.getFirstName() == null)
            applicantContacts.setFirstName(client.getFirstName());
        if (applicantContacts.getMiddleName() == null)
            applicantContacts.setMiddleName(client.getMiddleName());
        if (applicantContacts.getLastName() == null)
            applicantContacts.setLastName(client.getLastName());
        clientContacts.put("commType", applicantContacts.getCommType());
        clientContacts.put("email", applicantContacts.getEmail());
        clientContacts.put("firstName", applicantContacts.getFirstName());
        clientContacts.put("middleName", applicantContacts.getMiddleName());
        clientContacts.put("lastName", applicantContacts.getLastName());
        clientContacts.put("phone", applicantContacts.getPhone());
        return clientContacts;
    }


    public RsRequest add(RqRequest request) throws GException {

        if (request.getClientContacts() == null)
            throw new GBadRequest();

        if (request.getData() != null) {
            // проверим тематику и поля в запросе
            requestFieldSubjectService.validateSubjectsAndField(request.getSubject(), request.getSubSubject(), request.getData());
        }

        // проверяем есть ли такой клиент, если нет то создаем нового
        Client client = clientService.editClient(request.getClientContacts());

        // создадим map с контактами клиента и заполним его
        Map<String, Object> clientContacts = getClientContactsMap(request, client);

        // проверим ссылку на существующий ОЗ
        if (request.getLink() != null && request.getLink() > 0) {
            cache.find(request.getLink());
        }

        //привяжем admission если такое существует
        Long admission = null;
        if (request.getCallIdentifier() != null) {
            admission = admissionService
                    .getByCallIdOrId(null, request.getCallIdentifier()).getId();
        }
        // сохранить запись в таблицу requests
        Request requestEntity =
                Request.getFromIncomingRequest(request, client, clientContacts, admission);
        setPlatform(requestEntity);

        requestStatusChangeService.setLifespans(requestEntity);

        requestCache.save(requestEntity);

        //Зарегестрируем создание ОЗ в истории обращения
        if (admission != null) {
            admissionEventService.registerDefRequestEvent(admissionCache.find(admission),
                    AdmissionEventTypes.DEFERRED_RQ_REGISTRATION, requestEntity);
        }
        eventService.createRequestCreatedEvent(requestEntity.getId());

        return RsRequest.get(requestEntity);
    }

    public RsRequest edit(RqEditRequest rqRequest) throws GException {

        Request request = cache.find(rqRequest.getId());
        if (rqRequest.getSubject() != null) {
            if (rqRequest.getSubSubject() != null) {
                requestFieldSubjectService.validateSubjectsAndField(rqRequest.getSubject(), rqRequest.getSubSubject(), rqRequest.getData());
                request.setSubSubject(request.getSubSubject());
            } else
                requestFieldSubjectService.validateSubjectsAndField(rqRequest.getSubject(), 0L, rqRequest.getData());
            request.setSubject(rqRequest.getSubject());
        }
        if (rqRequest.getClientContacts() != null) {
            Client client = clientService.editClient(rqRequest.getClientContacts());
            clientCache.save(client);

            // создадим map с контактами клиента и заполним его
            Map<String, Object> clientContacts = getClientContactsMap(rqRequest, client);
            request.setApplicantContacts(clientContacts);
            request.setRequester(client.getId());
        }
        request.setChannel(rqRequest.getChannel());
        request.setText(rqRequest.getText());

        // todo уточнить про изменение номера обращения
        request.setAdmission(rqRequest.getAdmission());
        request.setBody(rqRequest.getData());
        if (rqRequest.getStatus() != null) {
            request.setStatus(rqRequest.getStatus());
            requestStatusChangeService.setLifespans(request);
        }
        return RsRequest.get(cache.save(request));
    }

    public RsRequest get(Long id) throws GNotAllowed, GNotFound {
        Request request = cache.find(id);
        Status status = statusCache.find(request.getStatus());
        List<RsEvent> events = eventService.getByRequest(id);
        RsRequest rsRequest = RsRequest.get(request);
        rsRequest.setEvents(events);
        rsRequest.setStatus(status.getCode() + ":" + status.getName());
        return rsRequest;
    }

    public Page<Request> getAll(RequestFilter rq) {
        return repo.findAll(new PageRequest(rq.getPageNumber(), rq.getPageSize()));
    }

    public void delete(Long id) throws GException {
        Request req = cache.find(id);
        req.setDisabled(new Date());
        cache.save(req);
    }

    public RsRequestComment addComment(Long request, String text) throws GNotAllowed, GNotFound, GSystemError {
        requestCache.find(request);
        RequestComment comment = RequestComment.builder()
                .comment(text)
                .author(context.getUser())
                .request(request)
                .build();
        comment = requestCommentsCache.save(comment);
        eventService.createCommentEvent(request, comment);
        return RsRequestComment.getFromEntity(comment);
    }

    public List<RsRequestComment> getAllByRequest(Long request) {
        return requestCommentsRepository.findAllByRequest(request).stream()
                .map(RsRequestComment::getFromEntity).collect(Collectors.toList());
    }
}
