package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.exceptions.GSystemError;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.manager_status_change.ManagerStatusChange;
import app.expert.db.manager_status_change.ManagerStatusChangeCache;
import app.expert.db.manager_status_info.ManagerStatusInfo;
import app.expert.db.manager_status_info.ManagerStatusInfoCache;
import app.expert.db.statics.manager_status.ManagerStatus;
import app.expert.db.statics.manager_status.ManagerStatusCache;
import app.expert.db.statics.manager_status.ManagerStatusRepository;
import app.expert.models.manager.RSStatusChange;
import app.expert.models.manager.RSStatusInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatusChangeService {

    private final ManagerCache managerCache;
    private final ManagerStatusCache statusCache;
    private final ManagerStatusInfoCache statusInfoCache;
    private final ManagerStatusChangeCache statusChangeCache;
    private final ManagerStatusRepository statusRepository;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Date getCurrentDateWithoutTime() throws GSystemError {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date currentDate;
        try {
            currentDate = formatter.parse(formatter.format(new Date()));
        } catch (ParseException ignore) {
            throw new GSystemError();
        }
        return currentDate;
    }

    public RSStatusInfo setStatus(String username, String agentId, String reasonCode, String workMode)
            throws GBadRequest, GNotFound, GNotAllowed, GSystemError {

        ManagerStatusChange statusChange;
        Manager manager;

        if ((username == null && agentId == null) || workMode == null || (workMode.equals("1") && reasonCode == null))
            throw new GBadRequest();

        if (username != null)
            manager = managerCache.findByUsername(username);
        else
            manager = managerCache.findByAgentId(agentId);

        List<String> statuses = statusCache.getAll().stream().filter(record -> {
            if (reasonCode != null)
                return record.getWorkMode().equals(workMode) &&
                        (record.getReasonCode() != null && record.getReasonCode().equals(reasonCode));
            else
                return record.getWorkMode().equals(workMode);
        }).map(ManagerStatus::getCode).collect(Collectors.toList());

        if (statuses.size() == 0) throw new GBadRequest();

        String status = statuses.get(0);
        logger.info("----------------" + status);

        // форматирование даты для поиска объекта statusInfo
        Date currentDate = getCurrentDateWithoutTime();
        // получить объект statusInfo за текущий день
        ManagerStatusInfo statusInfo = statusInfoCache.findByManagerAndDate(manager.getId(), currentDate);


        // если statusInfo == null,
        // то создаем новую запись и ставим для статуса время ноль
        // (при повторной смене статуса посчитается
        // сколько времени сотрудник пробыл в этом статусе)
        if (statusInfo == null) {
            Map<String, Object> statusAndTime = new HashMap<>();
            statusAndTime.put(status, 0L);
            statusInfo = ManagerStatusInfo.builder()
                    .currentStatus(status)
                    .manager(manager.getId())
                    .date(currentDate)
                    .modified(new Date())
                    .info(statusAndTime)
                    .build();
        }

        /* если statusInfo != null -> посчитать время для предыдущего статуса
        (отнимаем поле modified от текущего времени)
            проверить есть ли в таблице status_change запись по дате, менеджеру и статусу
                если есть:
                    - проставить end_time
        */
        else {
            statusChange = statusChangeCache.findByManagerAndDateAndStatusAndEndNull(
                    manager.getId(), currentDate, statusInfo.getCurrentStatus());
            if (statusChange.getStatus().equals(status)) {
                return RSStatusInfo.get(manager.getUsername(), statusInfo, currentDate);
            }
            statusChange.setEnd(new Date());
            statusChangeCache.save(statusChange);

            statusInfo.calculateTimeForCurrentStatus();
            statusInfo.setModified(new Date());
            statusInfo.setCurrentStatus(status);
            if (statusInfo.getInfo().containsKey(status)) {
                statusInfo.getInfo().put(status, (((Double) statusInfo.getInfo().get(status)).longValue()));
            } else {
                statusInfo.getInfo().put(status, 0L);
            }
        }

        statusChangeCache.save(ManagerStatusChange.get(
                currentDate,
                manager.getId(),
                status,
                statusInfo.getModified()));
        statusInfoCache.save(statusInfo);
        return RSStatusInfo.get(manager.getUsername(), statusInfo, currentDate);
    }

    public RSStatusInfo getStatusInfo(String username) throws GNotAllowed, GNotFound, GSystemError {

        // проверить что менеджер с таким username существует
        Manager manager = managerCache.findByUsername(username);

        // форматирование даты для поиска объекта statusInfo
        Date currentDate = getCurrentDateWithoutTime();

        // найти статус инфо
        ManagerStatusInfo statusInfo = statusInfoCache.findByManagerAndDate(manager.getId(), currentDate);
        if (statusInfo == null) throw new GNotFound();
        statusInfo.calculateTimeForCurrentStatus();
        statusInfoCache.save(statusInfo);
        return RSStatusInfo.get(manager.getUsername(), statusInfo, currentDate);
    }

    public List<RSStatusChange> getStatusInfoInInterval(String username, Date start, Date end)
            throws GNotAllowed, GNotFound, GSystemError {

        Manager manager = managerCache.findByUsername(username);

        // переводим даты в нужный формат
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.000000");
        try {
            start = dbDateFormat.parse(dbDateFormat.format(start));
            end = dbDateFormat.parse(dbDateFormat.format(end));
        } catch (ParseException e) {
            throw new GSystemError();
        }
        List<ManagerStatusChange> statusChangeList =
                statusChangeCache.findByManagerAndStartGreaterThanEqualAndEndLessThanEqual(manager.getId(), start, end);

        Date finalStart = start;
        Date finalEnd = end;
        return statusChangeList.stream()
                .filter(record -> {
            if (record.getEnd() == null) {
                return record.getStart().compareTo(finalStart) >= 0 && record.getStart().compareTo(finalEnd) <= 0;
            }
            return true;
        })
                .map(record ->
                RSStatusChange.builder()
                        .username(manager.getUsername())
                        .start(record.getStart())
                        .end(record.getEnd())
                        .status(record.getStatus())
                        .build()
        ).collect(Collectors.toList());
    }
}
