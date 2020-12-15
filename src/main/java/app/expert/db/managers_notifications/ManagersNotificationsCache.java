package app.expert.db.managers_notifications;

import app.base.db.GRedisCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagersNotificationsCache extends GRedisCache<Long, ManagerNotification, ManagersNotificationsRepository> {

    public ManagersNotificationsCache(ManagersNotificationsRepository repository) {
        super(repository, ManagerNotification.class);
    }

    public List<ManagerNotification> getAll() {
        return getRepository().findAll().stream().filter(x -> !x.isDisabled()).collect(Collectors.toList());
    }

}
