package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.exceptions.GSystemError;
import app.base.models.GResponse;
import app.base.utils.DateUtils;
import app.expert.db.managers_notifications.ManagerNotification;
import app.expert.db.managers_notifications.ManagersNotificationsCache;
import app.expert.db.managers_notifications.ManagersNotificationsRepository;
import app.expert.db.statics.manager_role.ManagerRoleCache;
import app.expert.models.managers_notifications.RqEditManagerNotification;
import app.expert.models.managers_notifications.RqManagerNotification;
import app.expert.models.managers_notifications.RqManagerNotificationFilter;
import app.expert.models.managers_notifications.RsManagerNotification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static app.base.constants.GErrors.OK;

@Service
@RequiredArgsConstructor
public class ManagersNotificationsService {

    private final ManagersNotificationsCache cache;
    private final ManagerRoleCache managerRoleCache;
    private final ManagersNotificationsRepository managersNotificationsRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * создание уведомления
     * @param request - модель запроса (роли - для кого уведомление, время исполнения, текст)
     * @return - созданное уведомление (response модель)
     * @throws GNotAllowed - если роли недоступны
     * @throws GNotFound - если роль не найдена
     * @throws GSystemError - если ошибка в парсинге даты
     */

    public GResponse add(RqManagerNotification request) throws GNotAllowed, GNotFound, GSystemError {

        // надо проверить что роли существуют, если роли нет то метод выкинет исклюение
        for (String role : request.getRoles()) {
            managerRoleCache.find(role);
        }

        Date now = DateUtils.convert(new Date(), DateUtils.HUMAN_DATE_TIME);

        for (String role : request.getRoles()) {
            ManagerNotification notification = ManagerNotification.builder()
                    .created(now)
                    .notification(request.getNotification())
                    .body(request.getText())
                    .title(request.getTitle())
                    .role(role)
                    .build();
            cache.save(notification);
        }
        return new GResponse(OK);
    }

    public GResponse disable(Long id) throws GNotAllowed, GNotFound, GSystemError {
        Date now = DateUtils.convert(new Date(), DateUtils.HUMAN_DATE_TIME);
        ManagerNotification notification = cache.find(id);
        notification.setDisabled(now);
        cache.save(notification);
        return new GResponse(OK);
    }

    /**
     * запрос всех уведомлений с фильтрами
     *(если фильтр пустой то возвращаются все уведомления кроме disabled)
     * @param filter - фильтр
     *               (начало и/или конец времени исполнения уведомления, роль)
     * @return список уведомлений (из респонс моделей)
     * отсортированный в порядке возрастания времени исполнения
     * @throws GNotAllowed если роль из фильтра не доступна
     * @throws GNotFound если роль из фильтра не найдена
     */
    public List<RsManagerNotification> getAllByFilter(RqManagerNotificationFilter filter) throws GNotAllowed, GNotFound {

        List<ManagerNotification> allNotifications = null;

        if (filter.getRole() != null && filter.getStart() != null && filter.getEnd() != null) {
            allNotifications = managersNotificationsRepository
                    .findAllByRoleAndNotificationGreaterThanOrEqualToAndNotificationLessThanOrEqualTo(filter.getRole(), filter.getStart(), filter.getEnd());

        } else if (filter.getRole() != null && filter.getStart() != null) {
            allNotifications = managersNotificationsRepository.findAllByRoleAndNotificationOnGreaterThanOrEqualTo(filter.getRole(), filter.getStart());

        } else if (filter.getRole() != null && filter.getEnd() != null) {
            allNotifications = managersNotificationsRepository.findAllByRoleAndNotificationOnLessThanOrEqualTo(filter.getRole(), filter.getEnd());

        } else if (filter.getRole() != null) {
            allNotifications = managersNotificationsRepository.findAllByRole(filter.getRole());

        } else {
            allNotifications = cache.getAll();
        }
        return allNotifications.stream().map(RsManagerNotification::get).collect(Collectors.toList());
    }

    /**
     * редактирование уведомления
     * @param request - модель содержит айди уведомления
     *                и новые данные(роли, текст, время исполнения)
     * @return - обновленное уведомление
     * @throws GNotAllowed - если уведомление или роли не доступны
     * @throws GNotFound - если уведомление или роли не найдены
     */
    public RsManagerNotification edit(RqEditManagerNotification request) throws GNotAllowed, GNotFound {

        for (String role : request.getRoles()) {
            managerRoleCache.find(role);
        }
        ManagerNotification notification = cache.find(request.getId());
        notification.setBody(request.getText());
        notification.setNotification(request.getNotification());
        notification.setTitle(request.getTitle());
        notification = cache.save(notification);
        for (String role : request.getRoles()) {
            notification.setRole(role);
            cache.save(notification);
        }
        return RsManagerNotification.get(notification);
    }

    public RsManagerNotification get(Long id) throws GBadRequest, GNotAllowed, GNotFound {
        return RsManagerNotification.get(cache.find(id));
    }
}
