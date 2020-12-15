package app.expert.services;


import app.base.exceptions.GBadRequest;
import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.services.GContextService;
import app.base.utils.EncryptionUtils;
import app.base.utils.ObjectUtils;
import app.expert.constants.DayTypes;
import app.expert.constants.Platform;
import app.expert.db.call.Call;
import app.expert.db.call.CallCache;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.manager_status_info.ManagerStatusInfo;
import app.expert.db.manager_status_info.ManagerStatusInfoCache;
import app.expert.db.statics.manager_role.ManagerRole;
import app.expert.db.statics.manager_status.ManagerStatusCache;
import app.expert.db.statics.subjects.Subject;
import app.expert.db.statics.subjects.SubjectCache;
import app.expert.models.manager.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ManagerService {

    private final ManagerCache cache;
    private final ManagerRoleService managerRoleService;
    private final SubjectCache subjectCache;
    private final ManagerStatusInfoCache statusInfo;
    private final ManagerStatusCache statusCache;
    private final ManagerSubjectService managerSubjectService;

    private final CallCache callCache;
    private final GContextService context;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private RSManager getRSManager(Manager manager) {
        try {// найти роль менеджера из БД
            ManagerRole managerRole = managerRoleService.get(manager.getRole());
            // положить роль в объект response
            RSRole rsRole = RSRole.get(managerRole);

            // получить статус менеджера
            String statusCode = null;
            String statusName = null;
            try {
                ManagerStatusInfo info = statusInfo.findByManagerAndDate(manager.getId(), new Date());
                statusCode = info == null ? null : info.getCurrentStatus();
                if (statusCode != null)
                    statusName = statusCache.find(statusCode).getName();
            } catch (GNotFound ignored) {}

            // вернуть response объект менеджера
            return RSManager.get(manager, rsRole, statusCode, statusName);
        } catch (GException e) {
            return null;
        }
    }

    public RSManager get(Long id) throws GNotFound, GNotAllowed, GBadRequest {
        Manager manager = cache.find(id);
        return getRSManager(manager);
    }

    //TODO: оптимизировать поиск
    public List<RSManager> getAll(String role, Platform platform) {
        return cache.getAll().stream()
                .filter(manager -> (ObjectUtils.isNull(role) || manager.getRole().equalsIgnoreCase(role))
                        && (ObjectUtils.isNull(platform) || manager.getPlatform().equals(platform.getKey())))
                .map(this::getRSManager)
                .collect(Collectors.toList());
    }

    public void checkIfExists(Long id) throws GNotAllowed, GNotFound {
        cache.find(id);
    }

    public boolean checkIfExistsAndWorkToday(Long id) throws GNotAllowed, GNotFound {
        Manager manager = cache.find(id);
        return manager.getWorkDayStatus().equals(DayTypes.WORK_DAY.getKey());
    }

    public List<Long> getManagersByUsername(List<String> managersUsernames) throws GNotFound, GNotAllowed, GBadRequest {
        List<Long> managers = new ArrayList<>(managersUsernames.size());
        for (String username : managersUsernames) {
            Manager manager = cache.findByUsername(username);
            managers.add(manager.getId());
        }
        return managers;
    }

    public List<Long> getManagersByUsername(Set<String> managersUsernames) throws GNotFound, GNotAllowed, GBadRequest {
        List<Long> managers = new ArrayList<>(managersUsernames.size());
        for (String username : managersUsernames) {
            Manager manager = cache.findByUsername(username);
            if (manager.getWorkDayStatus().equals(DayTypes.WORK_DAY.getKey()))
                throw new GBadRequest();
            managers.add(manager.getId());
        }
        return managers;
    }

    public RSManager add(RQCreateManager managerInfo) throws GException {
        // проверить что роль существует
        ManagerRole role = managerRoleService.get(managerInfo.getRoleCode());

        // создать менеджера
        Manager manager = Manager.createManager(managerInfo);
        cache.save(manager);

        managerSubjectService.setSubjects(manager.getId(),
                subjectCache.getAll().stream().map(Subject::getId).collect(Collectors.toList()));

        // вернуть RSManager
        return getRSManager(manager);
    }

    public RSManager update(RQEditManager managerInfo) throws GException {
        Manager manager = cache.findByUsername(managerInfo.getUsername());

        // если роль новая то проверить существование и поменять
        String role = managerInfo.getRole();
        if (!ObjectUtils.isBlank(role)) {
            ManagerRole managerRole = managerRoleService.get(role);
            manager.setRole(managerRole.getCode());
        }
        // обновить остальную информацию и сохранить
        manager = manager.update(managerInfo);
        cache.save(manager);
        return getRSManager(manager);
    }

    public void delete(Long id) throws GException {
        Manager manager = cache.find(id);
        manager.setDisabled(new Date());
        cache.save(manager);
    }

    public RSManager addStationInfo(RqStationInfo rqStationInfo) throws GNotAllowed, GBadRequest, GNotFound {

        Manager manager = cache.findByUsername(rqStationInfo.getUsername());

        // проверим уникальность agentId запросом к базе, если такой agentId есть то выкинем ошибку
        if (rqStationInfo.getAgentId() != null) {
            try {
                if (!cache.findByAgentId(rqStationInfo.getAgentId()).getId().equals(manager.getId())) throw new GBadRequest();

                // метод findByAgentId выкинет ошибку если сотрудника нет,
                // в нашем случае все в порядке, поэтому игнорируем исключение
            } catch (GNotFound ignore) {
            }
            manager.setAgentId(rqStationInfo.getAgentId());
        }
        manager.setAgentPasswordHash(EncryptionUtils.encrypt(rqStationInfo.getPassword()));
        manager.setStationId(rqStationInfo.getStationId());

        cache.save(manager);
        return getRSManager(manager);
    }

    /**
     * Возвращает статистику на сегодня для сотрудника:
     * Кол-во звонков, среднее время разговора, среднее время на удержании
     * @id - идентификатор сотрудника
     * @GNotFound - если не найден менеджер с таким id
     */
    public RsTodayManagerStat todayManagerStatistics(Long id) throws GNotAllowed, GNotFound {
        if(id == null)
            id = context.getUser();

        //Проверяем если ли менеджер с таким id
        cache.find(id);


        //Получаем все звонки для сотрудника,
        // если сзонки не найдены, возвращаем нулевые значения для всех полей
        List<Call> calls = callCache.getByManager(id);

        //Получаем все сегодняшние завершенные звонки
        calls = calls.stream()
                .filter((call) -> isToday(call.getStarted()))
                .filter((call) -> call.getOnHold() != null)
                .collect(Collectors.toList());

        if(calls.isEmpty())
            return RsTodayManagerStat.getDefault();

        //Получаем кол-во звонков, среднюю продолжительность звонков и время на удержании
        long callDuration = 0;
        long onHold = 0;
        for(Call call : calls) {
            callDuration += call.getEnded().getTime() - call.getStarted().getTime();
            onHold += call.getOnHold().getTime();
        }
        return RsTodayManagerStat.get(calls.size(), onHold/calls.size(), callDuration/calls.size());
    }

    private boolean isToday(Date date){
        LocalDate localDate = LocalDateTime.ofInstant(date.toInstant(), TimeZone.getDefault().toZoneId()).toLocalDate();
        LocalDate now = LocalDate.now(TimeZone.getDefault().toZoneId());
        return now.equals(localDate);
    }

    public List<String> getManagersAtWork() {
        return cache.getAll().stream()
                .filter(manager -> manager.getWorkDayStatus().equals(DayTypes.WORK_DAY.getKey()))
                .map(Manager::getUsername)
                .collect(Collectors.toList());
    }
}
