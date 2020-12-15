package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.db.manager.ManagerCache;
import app.expert.db.manager.ManagerRepository;
import app.expert.db.request.RequestCache;
import app.expert.db.request.RequestRepository;
import app.expert.db.request.RequestSearchEntityRepository;
import app.expert.db.request_reminders.RequestRemindersRepository;
import app.expert.db.schedule.manager_schedule.ManagerScheduleRepository;
import app.expert.db.statics.status.StatusCache;
import app.expert.models.statistics.*;
import org.springframework.aop.AopInvocationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


@RequiredArgsConstructor
@Service
public class StatService {

    private static final String ARRIVED = "Поступило";
    private static final String INPUT_CHANNEL = "По каналу поступления";
    private static final String IN_PROCESSED = "В работе";
    private static final String COMPLETED = "Завершенные";

    private static final String ARRIVE_DISTRIBUTED = "Распределено(в обработке)";
    private static final String ARRIVE_UNDISTRIBUTED = "Не распределено";

    private static final String CHANNEL_PHONE = "Телефон";
    private static final String CHANNEL_EMAIL = "Электронная почта";
    private static final String CHANNEL_ROSREESTRS_PORTAL = "Портал Росреестра";
    private static final String CHANNEL_ROSREESTRS_SITE = "Сайт Росреестра";
    private static final String CHANNEL_INFORMATION_SERVICE = "Сервис предоставления выписок";
    private static final String CHANNEL_OTHERS = "Иные каналы";

    private static final String PROCESS_ESROO = "Направлен в ЕСРОО";
    private static final String PROCESS_TO_PO = "Направлен в ЦА/ТО/ПО Росреестра";
    private static final String PROCESS_NEED_FOR_CLIENT_REQUEST = "Запрос доп.сведений у клиента";

    private static final String COMPLETED_SELF_DEPENDENT_PROCESSED = "Обработано специалистом самостоятельно";
    private static final String COMPLETED_ESROO_RESPONSE = "По результатам направления ответа из ЕСРОО";
    private static final String COMPLETED_TERM_VIOLATION = "По истечению регламентного срока обработки ОЗ";
    private static final String COMPLETED_MANAGER_DECISION = "По решению сотрудника";
    private static final String COMPLETED_OTHERS = "Иные";

    private final StatusCache statusCache;
    private final RequestCache requestCache;
    private final RequestRepository repository;
    private final ManagerCache managerCache;
    private final RequestRemindersRepository notificationsRepository;
    private final ManagerScheduleRepository scheduleRepository;
    private final ManagerRepository managerRepository;
    private final RequestSearchEntityRepository requestSearchEntityRepository;

    public RsStat getStat(RqStat rq) throws GBadRequest {
        if(rq.getPlatforms() == null){
            throw new GBadRequest();
        }
        RsStat response = new RsStat();
        List<Platform> platforms = new ArrayList<>();
        if(rq.getPlatforms().contains(Platform.KAZAN) && !rq.getPlatforms().contains(Platform.KURSK)){
            platforms.add(Platform.KAZAN);

            addArrived(response, Platform.KAZAN);
            addInProcess(response, platforms);
            addCompleted(response, Platform.KAZAN, platforms);
            addInputChannel(response, Platform.KAZAN);
            addManagerCount(response, Platform.KAZAN);
        } else if(!rq.getPlatforms().contains(Platform.KAZAN) && rq.getPlatforms().contains(Platform.KURSK)) {
            platforms.add(Platform.KURSK);

            addArrived(response, Platform.KURSK);
            addInProcess(response, platforms);
            addCompleted(response, Platform.KURSK, platforms);
            addInputChannel(response, Platform.KURSK);
            addManagerCount(response, Platform.KURSK);
        } else if (rq.getPlatforms().contains(Platform.KAZAN) && rq.getPlatforms().contains(Platform.KURSK)){

            addArrived(response, Platform.KAZAN);
            addArrived(response, Platform.KURSK);

            platforms.add(Platform.KURSK);
            platforms.add(Platform.KAZAN);
            addInProcess(response, platforms);

            addCompleted(response, Platform.KURSK, platforms);
            addCompleted(response, Platform.KAZAN, platforms);

            addInputChannel(response, Platform.KURSK);
            addInputChannel(response, Platform.KAZAN);

            addManagerCount(response, Platform.KAZAN);
            addManagerCount(response, Platform.KURSK);
        }
        addNotificationsCount(response);
        addAverageTimeCount(response);
        return response;
    }

    private void addArrived(RsStat rs, Platform platform){
        //Собираем статистику по принятым ОЗ за последний месяц
        rs.add(
                ARRIVED,
                ARRIVE_DISTRIBUTED,
                repository.countByStatusStateAndPlatform("IN_PROCESSING", platform.getKey(), getMonthAgoTimePoint()));
        rs.add(
                ARRIVED,
                ARRIVE_UNDISTRIBUTED,
                repository.countByStatusStateAndPlatform("PENDING", platform.getKey(), getMonthAgoTimePoint()));
    }

    private void addInProcess(RsStat rs, List<Platform> platforms){
        //Направлен в ЕСРОО
        List<String> statuses = Arrays.asList("201", "202", "203", "205", "210", "215", "204", "214");
        int count = repository.countByStatusInAndPlatformInAndCreatedAfter(statuses, platforms, getMonthAgoTimePoint());
        rs.add(IN_PROCESSED, PROCESS_ESROO , count);

        //Направленно в ЦА/ТО/ПО Росреестра
        statuses = Arrays.asList("216", "301", "302", "303", "501", "505", "401", "403");
        count = repository.countByStatusInAndPlatformInAndCreatedAfter(statuses, platforms, getMonthAgoTimePoint());
        rs.add(IN_PROCESSED, PROCESS_TO_PO , count);

        //#TODO Должен быть пункт "Запросить доп. сведения у заявителя"
    }

    private void addInputChannel(RsStat rs, Platform platform) {
        rs.add(
                INPUT_CHANNEL,
                CHANNEL_PHONE,
                repository.countByChannelAndPlatformAndCreatedAfter(Channel.PHONE, platform, getMonthAgoTimePoint()));
        rs.add(
                INPUT_CHANNEL, CHANNEL_EMAIL,
                repository.countByChannelAndPlatformAndCreatedAfter(Channel.EMAIL, platform, getMonthAgoTimePoint()));
        rs.add(
                INPUT_CHANNEL,
                CHANNEL_ROSREESTRS_PORTAL,
                repository.countByChannelAndPlatformAndCreatedAfter(Channel.ROSREESTR_PORTAL, platform, getMonthAgoTimePoint()));
        rs.add(
                INPUT_CHANNEL,
                CHANNEL_ROSREESTRS_SITE,
                repository.countByChannelAndPlatformAndCreatedAfter(Channel.ROSREESTRS_SITE, platform, getMonthAgoTimePoint()));
        rs.add(
                INPUT_CHANNEL,
                CHANNEL_INFORMATION_SERVICE,
                repository.countByChannelAndPlatformAndCreatedAfter(Channel.INFORMATION_SERVICE, platform, getMonthAgoTimePoint()));
        rs.add(
                INPUT_CHANNEL,
                CHANNEL_OTHERS,
                repository.countByChannelAndPlatformAndCreatedAfter(Channel.OTHER, platform, getMonthAgoTimePoint()));
    }

    private void addCompleted(RsStat rs, Platform platform, List<Platform> platforms){
        //Закрыто после самостоятельно обработки
        rs.add(COMPLETED,
                COMPLETED_SELF_DEPENDENT_PROCESSED,
                repository.countByStatusAndRouteTypeAndPlatform("CLOSED", "100", platform.getKey(), getMonthAgoTimePoint()));

        //Закрыто после предоставления ответа из ЕСРОО
        rs.add(
                COMPLETED,
                COMPLETED_ESROO_RESPONSE,
                repository.countByStatusAndRouteTypeAndPlatform("CLOSED", "200", platform.getKey(), getMonthAgoTimePoint()));

        //Истек срок обработки
        rs.add(
                COMPLETED,
                COMPLETED_TERM_VIOLATION,
                repository.countByStatusStateAndPlatform("EXPIRED", platform.getKey(), getMonthAgoTimePoint()));

        //Закрыто по решению сотрудника
        List<String> statuses = Arrays.asList("111", "211", "308", "408", "514");
        int count = repository.countByStatusInAndPlatformInAndCreatedAfter(statuses, platforms, getMonthAgoTimePoint());
        rs.add(COMPLETED, COMPLETED_MANAGER_DECISION, count);
    }

    private void addAverageTimeCount(RsStat rs){
        double count = 0;
        try {
            count = repository.computeAverageProcessTime(getMonthAgoTimePoint());
        } catch (AopInvocationException ignore){}
        rs.addAverage((int) count);
    }

    public Map<String, Object> countRequests(StatRequestFilter rq) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", requestSearchEntityRepository.count(rq.getRqFilter()));
        return result;
    }

    private void addNotificationsCount(RsStat rs) {
        //Считаем напоминания с текущего моммента и до полуночи след дня.
        int count = notificationsRepository.countBySignalBetween(getNow(), getTomorrow());
        rs.addNotifications(count);
    }

    private void addManagerCount(RsStat rs, Platform platform) {
        //Считаем кол-во сотрудников ООЗ, работающих сегодня
        int count = managerRepository.countByWorkDayStatusAndRole("WORK_DAY", "oooz");
        rs.addManagers(count);
    }

    public RsRequestStat getCommonRequestStat(Date from, Date to) {
        RsRequestStat stat = new RsRequestStat();
        stat.add(repository.findCommonRequestStat(from, to));
        return stat;
    }

    private Date getYesterday(){
        return Date.from(LocalDate.now().minusDays(1L).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date getNow(){
        LocalDateTime startOfDay = LocalDateTime.now();
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date getTomorrow(){
        LocalDate tomorrow = LocalDate.now().plusDays(1L);
        return Date.from(tomorrow.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date getMonthAgoTimePoint(){
        LocalDate point = LocalDate.now().minusMonths(1L);
        return Date.from(point.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
