package app.expert.db.manager_status_change;

import app.base.db.GRedisCache;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ManagerStatusChangeCache extends GRedisCache<Long, ManagerStatusChange, ManagerStatusChangeRepository> {

    public ManagerStatusChangeCache(ManagerStatusChangeRepository repository) {
        super(repository, ManagerStatusChange.class);
    }

    public ManagerStatusChange findByManagerAndDateAndStatusAndEndNull(Long manager, Date date, String status) {
        return getRepository().findFirstByManagerAndDateAndStatusAndEndNull(manager, date, status);
    }

    public List<ManagerStatusChange> findByManagerAndStartGreaterThanEqualAndEndLessThanEqual(Long manager, Date start, Date end) {
        return getRepository().findByManagerAndStartGreaterThanEqualAndEndLessThanEqualOrEndNull(manager, start, end);
    }
}
