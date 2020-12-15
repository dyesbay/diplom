package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.manager.ManagerRepository;
import app.expert.db.request.RequestRepository;
import app.expert.models.statistics.RqStat;
import app.expert.models.statistics.RsManagerStat;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ManagersStatService {

    private static final String COMPLETED_TODAY = "Количество завершенных запросов(за текущий день)";
    private static final String ESROO_TO_REQUESTS_TODAY =
            "Количество напрвленных в ЕСРОО запросов/" +
            "по эл.почте в ЦА/ТО/ПО Росреестра(за текущий день)";

    private static final String ESROO_REQUESTS =
            "Запросы," +
            " по которым нужно предоставить ответ заявителю " +
            "после получения информации по инциденту в ЕСРОО (общее количество)";

    private static final String LIFE_SPAN_VIOLATION =
            "Запросы в работе сотрудника," +
            " по которым нарушен один из сроков обработки," +
            " установленных Регламентом ВЦТО (нарушения на стороне сотрудника ВЦТО)";

    private final ManagerRepository managerRepo;

    private final ManagerCache managerCache;

    private final RequestRepository rqRepository;

    @Autowired
    private final EntityManagerFactory emf;

    public RsManagerStat getStat(RqStat rq) throws GBadRequest, GNotAllowed, GNotFound {
        RsManagerStat rs = new RsManagerStat();
        if(rq.getPlatforms() == null) {
            throw new GBadRequest();
        }
        if (!rq.getPlatforms().contains(Platform.KURSK) && rq.getPlatforms().contains(Platform.KAZAN)) {
            addChannelStat(rs, Platform.KAZAN);
            addCompleteTodayStat(rs, Platform.KAZAN);
            addRequiredAnswerAfterESROOResponse(rs, Platform.KAZAN);
            addLifeSpanStatus(rs, Platform.KAZAN);
        } else if (rq.getPlatforms().contains(Platform.KURSK) && !rq.getPlatforms().contains(Platform.KAZAN)) {
            addChannelStat(rs, Platform.KURSK);
            addCompleteTodayStat(rs, Platform.KURSK);
            addRequiredAnswerAfterESROOResponse(rs, Platform.KURSK);
            addLifeSpanStatus(rs, Platform.KURSK);
        } else if (rq.getPlatforms().contains(Platform.KURSK) && rq.getPlatforms().contains(Platform.KAZAN)) {
            addChannelStat(rs, Platform.KAZAN);
            addCompleteTodayStat(rs, Platform.KAZAN);
            addRequiredAnswerAfterESROOResponse(rs, Platform.KAZAN);
            addLifeSpanStatus(rs, Platform.KAZAN);
            addChannelStat(rs, Platform.KURSK);
            addCompleteTodayStat(rs, Platform.KURSK);
            addRequiredAnswerAfterESROOResponse(rs, Platform.KURSK);
            addLifeSpanStatus(rs, Platform.KURSK);
        } else {
            throw new GBadRequest();
        }
        return rs;
    }

    private void addChannelStat(RsManagerStat rs, Platform platform) throws GNotAllowed, GNotFound {
        EntityManager em = emf.createEntityManager();
        Query query = em.createNativeQuery(
                "select m.id,rq.channel from requests rq " +
                "inner join managers m on rq.assignee=m.id " +
                "where rq.created_on > :date and m.platform = :platform " +
                        "and m.role = 'oooz';")
                .setParameter("date", new Date(0L)) // #TODO выяснить за какой период нужно собирать стату
                .setParameter("platform", platform.getKey());

        List<Object[]> rows = query.getResultList();
        List<Manager> managers = managerCache.getAll()
                .stream()
                .filter(m -> m.getPlatform().equals(platform.getKey())
                        && m.getRole().equalsIgnoreCase("oooz"))
                .collect(Collectors.toList());
        for (Manager manager : managers) {
            for (Object[] row : rows) {
                Channel channel = Channel.valueOf((String) row[1]);
                if(((BigInteger) row[0]).longValue() == manager.getId()){
                    rs.add(manager, channel.getValue(), 1);
                } else {
                    rs.add(manager , channel.getValue(), 0);
                }
            }
            //Если в выборке были не все каналы поступления ОЗ
            //// то заполняем нулями все поля для всех менеджеров
            for (Channel channel : Channel.values())
                rs.add(manager , channel.getValue(), 0);
        }
        em.close();
    }

    private void addCompleteTodayStat(RsManagerStat rs, Platform platform) throws GNotAllowed, GNotFound {
        EntityManager em = emf.createEntityManager();
        Query query = em.createNativeQuery(
                "select m.id,m.platform from requests rq " +
                "inner join managers m on rq.assignee=m.id " +
                "where m.platform= :platform AND rq.closed_on between :yesterday AND :tomorrow " +
                        "and m.role = 'oooz' ;")
                .setParameter("yesterday", getYesterday())
                .setParameter("tomorrow", getTomorrow())
                .setParameter("platform", platform.getKey());

        List<Object[]> rows = query.getResultList();
        //Фильтруем менеджеров по платформам
        List<Manager> managers = managerCache.getAll().stream().filter(m -> m.getPlatform().equals(platform.getKey())
                && m.getRole().equalsIgnoreCase("oooz"))
                .collect(Collectors.toList());
        for (Manager manager : managers) {
            for (Object[] row : rows) {
                if(((BigInteger) row[0]).longValue() == manager.getId())
                    rs.add(manager , COMPLETED_TODAY, 1);
                else
                    rs.add(manager, COMPLETED_TODAY, 0);
            }
            //Если в выборке 0 строк,
            // то заполняем нулями все поля для всех менеджеров
            if(rows.size() == 0) {
                rs.add(manager, COMPLETED_TODAY, 0);
            }
        }
        em.close();
    }

    private void addRequiredAnswerAfterESROOResponse(RsManagerStat rs, Platform platform) throws GNotAllowed, GNotFound {
        EntityManager em = emf.createEntityManager();
        Query query = em.createNativeQuery(
                "select m.id,m.platform from requests rq " +
                        "inner join managers m on rq.assignee=m.id " +
                        "where m.platform= :platform AND rq.status = :status " +
                        "and m.role = 'oooz' ;")
                .setParameter("status", "205")
                .setParameter("platform", platform.getKey());

        List<Object[]> rows = query.getResultList();
        //Фильтруем менеджеров по платформам
        List<Manager> managers = managerCache.getAll().stream()
                .filter(m -> m.getPlatform().equals(platform.getKey()) && m.getRole()
                        .equalsIgnoreCase("oooz")).collect(Collectors.toList());
        for (Manager manager : managers) {
            for (Object[] row : rows) {
                if(((BigInteger) row[0]).longValue() == manager.getId())
                    rs.add(manager , ESROO_REQUESTS, 1);
                else
                    rs.add(manager, ESROO_REQUESTS, 0);
            }
            //Если в выборке 0 строк,
            // то заполняем нулями все поля для всех менеджеров
            if(rows.size() == 0) {
                rs.add(manager, ESROO_REQUESTS, 0);
            }
        }
        em.close();
    }

    private void addLifeSpanStatus(RsManagerStat rs, Platform platform) throws GNotAllowed, GNotFound {
        EntityManager em = emf.createEntityManager();
        Query query = em.createNativeQuery(
                "select m.id,m.platform from requests rq " +
                        "inner join managers m on rq.assignee=m.id " +
                        "where m.platform= :platform AND rq.status = :exp_status OR rq.status = :life_span_status " +
                        "and m.role = 'oooz' ;")
                .setParameter("exp_status", "191")
                .setParameter("life_span_status", "193")
                .setParameter("platform", platform.getKey());

        List<Object[]> rows = query.getResultList();
        //Фильтруем менеджеров по платформам
        List<Manager> managers = managerCache.getAll().stream().filter(m -> m.getPlatform().equals(platform.getKey())
                && m.getRole().equalsIgnoreCase("oooz"))
                .collect(Collectors.toList());
        for (Manager manager : managers) {
            for (Object[] row : rows) {
                if(((BigInteger) row[0]).longValue() == manager.getId())
                    rs.add(manager , LIFE_SPAN_VIOLATION, 1);
                else
                    rs.add(manager, LIFE_SPAN_VIOLATION, 0);
            }
            //Если в выборке 0 строк,
            // то заполняем нулями все поля для всех менеджеров
            if(rows.size() == 0) {
                rs.add(manager, LIFE_SPAN_VIOLATION, 0);
            }
        }
        em.close();
    }

    public RsManagerStat getStatForDistribution(RqStat rq) throws GNotAllowed, GNotFound {
        RsManagerStat rs = new RsManagerStat();
        if(rq.getPlatforms() == null)
            rq.setPlatforms(new ArrayList<>());
        EntityManager em = emf.createEntityManager();
        // Выборка из базы запросов назначенных сотрудникам и которые не закрыты
        Query query = em.createNativeQuery(
                "select m.id, rq.channel, rq.assigned_on " +
                        "from requests rq " +
                        "inner join managers m " +
                        "on m.id = rq.assignee " +
                        "inner join status st " +
                        "on st.code = rq.status " +
                        "where st.state != 'CLOSED' ;");

        List<Object[]> rows = query.getResultList();
        em.close();
        // Фильтруем менеджеров по платформам
        // и выбираем только сотрудников ООЗ
        List<Manager> managers = managerCache.getAll().stream().filter(m -> rq.getPlatforms().contains(Platform.valueOf(m.getPlatform()))
                && m.getRole().equalsIgnoreCase("oooz"))
                .collect(Collectors.toList());

        for (Manager manager : managers) {
            for (Object[] row : rows) {
                long id = ((BigInteger) row[0]).longValue();
                String platform = (String)row[1];
                Date date = (Date) row[2];
                //Проверяем совпадает ли id сотрудника с id assignee из базы
                if( id == manager.getId()) {
                    //Если канал поступления телефон, добавляем
                    if(platform.equals(Channel.PHONE.getKey())) {
                        rs.add(manager, Channel.PHONE.getKey(), 1);
                        //Если распределено сегодля, добавляем
                        if (assignedToday(date))
                            rs.add(manager, Channel.PHONE.getKey() + "_TODAY", 1);
                    }
                    //Если канал поступления почта, добавляем
                    if(platform.equals(Channel.EMAIL.getKey())) {
                        rs.add(manager, Channel.EMAIL.getKey(), 1);
                        //Если распределено сегодля, добавляем
                        if (assignedToday(date))
                            rs.add(manager, Channel.EMAIL.getKey() + "_TODAY", 1);
                    }
                    //Если канал поступления портал Росреестра
                    if(platform.equals(Channel.ROSREESTR_PORTAL.getKey())){
                        rs.add(manager, Channel.ROSREESTR_PORTAL.getKey(), 1);
                        //Если распределено сегодля, добавляем
                        if (assignedToday(date))
                            rs.add(manager, Channel.EMAIL.getKey() + "_TODAY", 1);
                    }
                }
                //Если id трудника не совпадает, то заполняем все поля для него нулями
                else{
                    rs.add(manager , Channel.PHONE.getKey(), 0);
                    rs.add(manager , Channel.EMAIL.getKey(), 0);
                    rs.add(manager , Channel.ROSREESTR_PORTAL.getKey(), 0);
                    //#TODO моковые данные
                    rs.add(manager , "ROSREESTR_MAIL", 0);

                    rs.add(manager , Channel.PHONE.getKey() + "_TODAY", 0);
                    rs.add(manager , Channel.EMAIL.getKey() + "_TODAY", 0);
                    rs.add(manager , Channel.ROSREESTR_PORTAL.getKey() + "_TODAY", 0);
                    //#TODO моковые данные
                    rs.add(manager , "ROSREESTR_MAIL" + "_TODAY", 0);
                }
            }
            //Если в выборке 0 строк,
            // то заполняем нулями все поля для всех сотрудников
            if(rows.size() == 0) {
                rs.add(manager , Channel.PHONE.getKey(), 0);
                rs.add(manager , Channel.EMAIL.getKey(), 0);
                rs.add(manager , Channel.ROSREESTR_PORTAL.getKey(), 0);
                //#TODO моковые данные
                rs.add(manager , "ROSREESTR_MAIL", 0);

                rs.add(manager , Channel.PHONE.getKey() + "_TODAY", 0);
                rs.add(manager , Channel.EMAIL.getKey() + "_TODAY", 0);
                rs.add(manager , Channel.ROSREESTR_PORTAL.getKey() + "_TODAY", 0);
                //#TODO моковые данные
                rs.add(manager , "ROSREESTR_MAIL" + "_TODAY", 0);
            }
        }
        return rs;
    }

    private boolean assignedToday(Date date) {
        if(date == null)
            return false;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date).equals(formatter.format(new Date()));
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
