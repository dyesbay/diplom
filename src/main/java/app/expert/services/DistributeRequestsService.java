package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.exceptions.GSystemError;
import app.base.services.GContextService;
import app.expert.constants.Channel;
import app.expert.constants.ExpertErrors;
import app.expert.constants.Platform;
import app.expert.constants.State;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.request.Request;
import app.expert.db.request.RequestCache;
import app.expert.db.request.RequestRepository;
import app.expert.db.statics.status.Status;
import app.expert.db.statics.status.StatusCache;
import app.expert.models.RqReassign;
import app.expert.models.RqReassignManager;
import app.expert.models.request.RsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistributeRequestsService {

    private final RequestCache requestCache;
    private final ManagerService managerService;
    private final ManagerCache managerCache;
    private final RequestRepository requestRepository;
    private final ManagerSubjectService managerSubjectService;
    private final RequestStatusChangeService requestStatusChangeService;
    private final EventService eventService;
    private final GContextService context;
    private final EntityManager em;
    private final StatusCache statusCache;

    private Logger logger = Logger.getLogger(this.getClass().getName());


    public List<Request> findSimilar(Request req, List<Request> requests) throws GNotAllowed, GNotFound {
        List<Request> similar = new ArrayList<>();
        similar.add(req);
        for (Request request : requests) {
            Status status = statusCache.find(request.getStatus());
            if (status.getState().equals(State.EXPIRED)) {
                continue;
            }
            if (!request.getId().equals(req.getId()) && request.getSubject().equals(req.getSubject())) {
                similar.add(request);
            }
        }
        return similar;
    }

    private List<Request> getRequests(List<Channel> channels, Platform platform) {
        List<Request> requestList = new ArrayList<>();
        for (Channel channel : channels) {
            List<Request> requests = requestRepository.findByAssigneeIsNullAndPlatformAndChannelOrderByCreatedAsc(platform, channel);
            if (requests != null)
                requestList.addAll(requests);
        }
        return requestList;
    }

    private List<Request> getRequestsBySubject(List<Channel> channels, Platform platform, Long subject) {
        List<Request> requestList = new ArrayList<>();
        for (Channel channel : channels) {
            List<Request> requests =
                    requestRepository.findByAssigneeIsNullAndPlatformAndChannelAndSubjectOrderByCreatedAsc(platform, channel, subject);
            if (requests != null)
                requestList.addAll(requests);
        }
        return requestList;
    }

    /**
     * функция для поиска запросов которые назначены сотруднику и находятся в процессе обработки
     *
     * @param manager  - сотрдуник которому назначены запросы
     * @param channels - каналы поступления запроса
     * @param platform - платформа
     * @return - список запросов
     */
    private List<Request> getRequestsByManager(Long manager, List<Channel> channels, Platform platform) {
        TypedQuery<Request> query = em.createQuery(
                "SELECT r FROM Request r where r.status in (select code from Status where state = 'IN_PROCESSING')" +
                        " AND r.assignee = :manager AND r.platform = :platform AND r.channel IN :channels", Request.class);
        query.setParameter("manager", manager);
        query.setParameter("platform", platform);
        query.setParameter("channels", channels);
        return query.getResultList();
    }

    private void assign(Request request, Long manager, boolean system) throws GNotAllowed, GNotFound, GSystemError, GBadRequest {
        request.setAssignee(manager);
        request.setAssigned(new Date()); // Устанавливаем дату назначения ОЗ сотруднику
        if (!system)
            requestStatusChangeService.changeStatus(request, "103");
        else
            requestStatusChangeService.changeStatusSystem(request, "103");
    }

    private void assignRequests(List<Request> requests, List<Long> managers, List<RsRequest> rsRequests, boolean system)
            throws GNotAllowed, GNotFound, GSystemError, GBadRequest {

        int i = 0;
        for (Request request : requests) {
            assign(request, managers.get(i), system);

            i = i + 1 == managers.size() ? 0 : i + 1;
            rsRequests.add(RsRequest.get(request));
        }
    }


    private void assignNumberOfRequests(List<Request> requests, List<Long> managers, List<RsRequest> rsRequests, Long num, boolean system)
            throws GNotAllowed, GNotFound, GSystemError, GBadRequest {

        int i = 0;
        int c = 0;
        for (Request request : requests) {

            assign(request, managers.get(i), system);
            i = i + 1 == managers.size() ? 0 : i + 1;
            c++;
            rsRequests.add(RsRequest.get(request));

            if (c == num)
                break;
        }
    }

    /**
     * назначает отложенным запросам сотрудников которые будут этот запрос обрабатывать
     *
     * @param managersUsernames - список сотрудников которым можно назначить запросы
     * @param system            - костыль для обозначения системных событий которые происходят, например, по расписанию
     * @return - список назначенных запросов
     */
    public List<RsRequest> distribute(List<String> managersUsernames, boolean system) throws GNotFound, GNotAllowed, GBadRequest, GSystemError {

        // убедимся что в списке managers все
        // сотрудники существуют и их текущий день рабочий, метод выкинет исключение если какой то айди не найден

        List<Long> managers = managerService.getManagersByUsername(managersUsernames);

        // вытащим все неназначенные запросы отсортированные по дате создания
        List<Request> requests = requestRepository.findByAssigneeIsNullOrderByCreatedAsc();
        List<RsRequest> rsRequests = new ArrayList<>(requests.size());

        for (Request req : requests) {

            Status status = statusCache.find(req.getStatus());
            if (status.getState().equals(State.EXPIRED)) {
                continue;
            }

            if (req.getAssignee() == null) {
                // найдем все запросы по этой тематике
                List<Request> similarRequests = findSimilar(req, requests);

                // найдем всех менеджеров которые могут работать с этой тематикой
                // todo учитывать что раньше кому то могли назначить запрос а кому то нет,
                //  последние должны быть вначале списка
                List<Long> managersForSubject = managerSubjectService
                        .getAllManagersBySubject(req.getSubject()).stream()
                        .filter(managers::contains).collect(Collectors.toList());

                // если сотрудников нет то возвращаем ошибку
                if (managersForSubject.size() == 0) throw new GBadRequest(ExpertErrors.NOT_FOUND_MANAGERS_FOR_REQUESTS);

                // назначить запросы
                assignRequests(similarRequests, managersForSubject, rsRequests, system);
            }

        }
        // вернем список
        return rsRequests;
    }

    public List<RsRequest> assigneeManagersToRequests(List<String> managersUsernames, List<Long> requestsIds, List<Channel> channels,
                                                      Long totalRequestsNumber,
                                                      Long requestsNumberPerManager)
            throws GNotAllowed, GNotFound, GSystemError, GBadRequest {


        List<Long> managers = managerService.getManagersByUsername(managersUsernames);

        Platform platform = Platform.valueOf(managerCache.find(context.getUser()).getPlatform());
        List<RsRequest> rsRequestList = new ArrayList<>();

        // если нам прислали запросы то назначаем на них
        if (requestsIds != null) {

            List<Request> requests = new ArrayList<>(requestsIds.size());
            for (Long req : requestsIds) {
                requests.add(requestCache.find(req));
            }

            if (totalRequestsNumber == null || totalRequestsNumber == 0)
                assignRequests(requests, managers, rsRequestList, false);
            else if (totalRequestsNumber > 0) {
                assignNumberOfRequests(requests, managers, rsRequestList, totalRequestsNumber, false);
            }
        } // если totalRequestsNumber то распределяем общее количество
        else if (totalRequestsNumber != null) {

            List<Request> requests = getRequests(channels, platform);
            if (requests.size() > totalRequestsNumber) {
                assignNumberOfRequests(requests, managers, rsRequestList, totalRequestsNumber, false);
            } else {
                assignRequests(requests, managers, rsRequestList, false);
            }
        }
        // если requestsNumberPerManager то каждому сотруднику должно назначиться по такому кол-ву запросов
        else if (requestsNumberPerManager != null) {
            List<Request> requests = getRequests(channels, platform);
            int i = 0;
            int c = 0;
            for (Request request : requests) {
                if (i == managers.size())
                    break;
                assign(request, managers.get(i), false);
                c++;
                if (c == requestsNumberPerManager) {
                    c = 0;
                    i++;
                }
                rsRequestList.add(RsRequest.get(request));
            }
        }
        return rsRequestList;
    }

    public List<RsRequest> reassignManagersToRequests(RqReassign rqReassign)
            throws GNotAllowed, GNotFound, GSystemError, GBadRequest {


        List<String> managers = rqReassign.getManagers().stream().map(RqReassignManager::getUsername).collect(Collectors.toList());

        if (rqReassign.getRequests() == null)
            throw new GBadRequest();

        List<RsRequest> rsRequestList = new ArrayList<>();

        List<Request> requests = new ArrayList<>();
        for (Long req : rqReassign.getRequests()) {
            requests.add(requestCache.find(req));
        }

        if (rqReassign.getAuto()) {
            assignRequests(requests,
                    managerService.getManagersByUsername(managers), rsRequestList, false);
        } else {
            int requestIndex = 0;

            for (RqReassignManager rqReassignMap : rqReassign.getManagers()) {

                Manager manager = managerCache.findByUsername(rqReassignMap.getUsername());
                Integer reqNum = rqReassignMap.getNum();
                for (int i = 0; i < reqNum; i++) {
                    Request request = requests.get(requestIndex);
                    assign(request, manager.getId(), false);
                    requestIndex++;
                    rsRequestList.add(RsRequest.get(request));
                }
            }
        }

        return rsRequestList;
    }
}
