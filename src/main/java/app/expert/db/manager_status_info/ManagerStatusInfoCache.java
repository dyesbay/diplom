package app.expert.db.manager_status_info;

import app.base.db.GRedisCache;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ManagerStatusInfoCache extends GRedisCache<Long, ManagerStatusInfo, ManagerStatusInfoRepository> {

    public ManagerStatusInfoCache(ManagerStatusInfoRepository repository) {
        super(repository, ManagerStatusInfo.class);
    }

    public ManagerStatusInfo findByManagerAndDate(Long manager, Date date) {
        return getRepository().findFirstByManagerAndDate(manager, date);
    }
}
