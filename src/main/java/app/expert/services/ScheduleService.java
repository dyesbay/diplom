package app.expert.services;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.models.GResponse;
import app.base.utils.DateUtils;
import app.expert.constants.DayTypes;
import app.expert.constants.ExpertErrors;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.schedule.general_schedule.GeneralSchedule;
import app.expert.db.schedule.general_schedule.GeneralScheduleCache;
import app.expert.db.schedule.general_schedule.GeneralScheduleRepository;
import app.expert.db.schedule.manager_schedule.ManagerSchedule;
import app.expert.db.schedule.manager_schedule.ManagerScheduleCache;
import app.expert.db.schedule.manager_schedule.ManagerScheduleRepository;
import app.expert.models.manager.schedule.RqEditManagerSchedule;
import app.expert.models.manager.schedule.RsManagerSchedule;
import app.expert.models.manager.schedule.RsScheduleDay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static app.base.constants.GErrors.OK;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ManagerScheduleCache managerScheduleCache;
    private final ManagerScheduleRepository managerScheduleRepository;

    private final GeneralScheduleCache generalScheduleCache;
    private final GeneralScheduleRepository generalScheduleRepository;
    private final ManagerCache managerCache;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private Date increment(Date date) {
        return DateUtils.convert(new Date(date.getTime() + 24 * 60 * 60 * 1000L), DateUtils.HUMAN_DATE);
    }

    /**
     * вспомогательный мтеод для обработки данных
     *
     * @param datesList   - список дат определенного типа
     * @param date        - дата для обработки
     * @param mainType    - тип дня в списке datesList
     * @param anotherType - противоположный mainType по смыслу тип дня
     */
    private void processDay(List<Date> datesList, Date date, String mainType, String anotherType) {

        // ищем запись по дате
        GeneralSchedule gs = generalScheduleCache.findByDate(date);

        // если в списке есть эта дата то установим для этой даты тип дня (зависит от списка) и сохраним
        if (datesList.contains(date)) {
            if (gs != null) {
                gs.setType(mainType);
                generalScheduleCache.save(gs);
            } else {
                generalScheduleCache.save(GeneralSchedule.builder()
                        .type(mainType)
                        .date(date)
                        .build());
            }
        } else {
            // если в списке нет даты то установим противоположный по смыслу тип дня и сохраним
            if (gs != null) {
                gs.setType(anotherType);
                generalScheduleCache.save(gs);
            } else {
                generalScheduleCache.save(GeneralSchedule.builder()
                        .type(anotherType)
                        .date(date)
                        .build());
            }
        }
    }

    public GResponse addWorkDaysForAll(Date from, Date to, List<Date> workDays) {


        for (Date date = from; date.before(to); date = increment(date)) {
            processDay(workDays, date, DayTypes.WORK_DAY.getKey(), DayTypes.WEEKEND.getKey());
        }
        return new GResponse(OK);
    }

    public GResponse addWeekendsForAll(Date from, Date to, List<Date> weekends) {

        for (Date date = from; date.before(to); date = increment(date)) {
            processDay(weekends, date, DayTypes.WEEKEND.getKey(), DayTypes.WORK_DAY.getKey());
        }
        return new GResponse(OK);
    }

    private void setDays(List<Date> dates, Date from, Date to, String dateType, Manager manager) {

        for (Date date = from; date.before(to); date = increment(date)) {

            if (manager.getDateOfDismissal() != null && date.compareTo(manager.getDateOfDismissal()) >= 0) {
                return;
            }
            if (!dates.contains(date))
                return;
            // ищем запись по дате
            ManagerSchedule gs = managerScheduleCache.findByDate(date);
            if (gs != null) {
                gs.setType(dateType);
                managerScheduleCache.save(gs);
            } else {
                managerScheduleCache.save(ManagerSchedule.builder()
                        .date(date)
                        .type(dateType)
                        .manager(manager.getId())
                        .build());
            }
        }
    }

    public GResponse editManagerSchedule(RqEditManagerSchedule request) throws GNotFound, GNotAllowed {

        Manager manager = managerCache.find(request.getManagerId());

        if (manager.getDateOfDismissal() != null && request.getFrom().compareTo(manager.getDateOfDismissal()) >= 0)
            return new GResponse(ExpertErrors.MANAGER_DISMISSAL_DATE);

        if (request.getWorkdays() != null)
            setDays(request.getWorkdays(), request.getFrom(), request.getTo(), DayTypes.WORK_DAY.getKey(), manager);
        if (request.getWeekends() != null)
            setDays(request.getWeekends(), request.getFrom(), request.getTo(), DayTypes.WEEKEND.getKey(), manager);
        if (request.getSickLeave() != null)
            setDays(request.getSickLeave(), request.getFrom(), request.getTo(), DayTypes.SICK_LEAVE.getKey(), manager);
        if (request.getVacation() != null)
            setDays(request.getVacation(), request.getFrom(), request.getTo(), DayTypes.VACATION.getKey(), manager);

        return new GResponse(OK);
    }

    private ManagerSchedule findSpecifiedDay(Date date, List<ManagerSchedule> managerSchedules) {
        for (ManagerSchedule day : managerSchedules) {
            if (day.getDate().equals(date))
                return day;
        }
        return null;
    }

    private RsManagerSchedule mergeLists(List<GeneralSchedule> generalSchedule, List<ManagerSchedule> managerSchedule, Date dateOfDismissal) {

        int i = 1;

        RsManagerSchedule rsManagerSchedule = RsManagerSchedule.builder().build();
        Map<String, RsScheduleDay> rsScheduleDays = new LinkedHashMap<>(generalSchedule.size());
        for (GeneralSchedule day : generalSchedule) {
            Date date = day.getDate();
            ManagerSchedule specifiedDay = null;

            // если в списке с расписанием менеджера есть эта дата то вставляем ее инфу, если нет то из главного расписания
            // заходим в метод поиска только если дата увольнения null или она позже чем дата в текущем цикле
            if (dateOfDismissal == null || date.compareTo(dateOfDismissal) < 0)
                specifiedDay = findSpecifiedDay(date, managerSchedule);

            RsScheduleDay scheduleDay = RsScheduleDay.builder().date(date).build();

            // если нашли что то в индивидуальном графике то берем инфу от туда
            if (specifiedDay != null) {
                scheduleDay.setDayType(specifiedDay.getType());

                // если дата в этом цикле равна или больше чем день увольнения то ставим тип дня DISMISSED
            } else if (dateOfDismissal != null && date.compareTo(dateOfDismissal) >= 0) {
                scheduleDay.setDayType("DISMISSED");
            }
            // в остальных случаях берем тип дня из общего графика
            else {
                scheduleDay.setDayType(day.getType());
            }
            rsScheduleDays.put("day" + i, scheduleDay);
            i++;
        }

        // сортируем список по датам
        rsManagerSchedule.setDays(rsScheduleDays);
        return rsManagerSchedule;
    }

    public List<RsManagerSchedule> getSchedule(Date from, Date to) {

        // TODO PLATFORM
        List<Manager> managers = managerCache.getAll();

        //  сделать запрос к табл с общим графиком, собрать в список

        List<GeneralSchedule> generalSchedule = generalScheduleRepository.findByDateGreaterThanEqualAndDateLessThan(from, to)
                .stream().sorted(Comparator.comparing(GeneralSchedule::getDate)).collect(Collectors.toList());

        List<RsManagerSchedule> schedule = new ArrayList<>(generalSchedule.size());
        // пройти списком по всем сотрудникам, для каждого сделать запрос к таблице с индивидуальным графиком
        for (Manager manager : managers) {
            List<ManagerSchedule> managerSchedule;


            managerSchedule = managerScheduleRepository.findByDateGreaterThanEqualAndDateLessThanAndManager(from, to, manager.getId());
            if (managerSchedule != null && managerSchedule.size() > 1) {
                managerSchedule = managerSchedule.
                        stream().sorted(Comparator.comparing(ManagerSchedule::getDate)).collect(Collectors.toList());
            }

            // пройти по списку с общим графиком и смерджить с индивидуальным графиком
            RsManagerSchedule rsManagerSchedule = mergeLists(generalSchedule, managerSchedule, manager.getDateOfDismissal());

            // заполнить остальную информацию
            rsManagerSchedule.setManager(manager.getId());
            rsManagerSchedule.setDateOfDismissal(manager.getDateOfDismissal());
            rsManagerSchedule.setFullName(manager.getFullName());
            rsManagerSchedule.setIsDismissed(manager.getDateOfDismissal() != null &&
                    manager.getDateOfDismissal().compareTo(from) >= 0 && manager.getDateOfDismissal().compareTo(to) <= 0);
            rsManagerSchedule.setShouldShow(true);
            schedule.add(rsManagerSchedule);
        }

        return schedule.stream().sorted(Comparator.comparing(RsManagerSchedule::getFullName)).collect(Collectors.toList());
    }
}
