package app.expert.services;

import app.base.constants.GErrors;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GException;
import app.base.utils.ObjectUtils;
import app.expert.db.statics.manager_status.ManagerStatus;
import app.expert.db.statics.manager_status.ManagerStatusCache;
import app.expert.db.statics.manager_status.ManagerStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerStatusService {

    private final ManagerStatusCache cache;
    private final ManagerStatusRepository repo;
    
    public ManagerStatus get(String code) throws GException {
        return cache.find(code);
    }

    public ManagerStatus add(String code, String name, String description) throws GException {
        cache.checkIfNotExists(code);
        return cache.save(ManagerStatus
                .builder()
                .code(code)
                .description(description)
                .name(name)
                .build());
    }

    public ManagerStatus edit(String code, String name, String description) throws GException {
        if (ObjectUtils.isBlank(code)) throw new GBadRequest(GErrors.BAD_REQUEST);
        ManagerStatus status = cache.find(code);
        status.setName(name);
        status.setDescription(description);
        return cache.save(status);
    }
    
    public void delete(String code) throws GException {
        ManagerStatus status = cache.find(code);
        status.setDisabled(new Date());
        cache.save(status);
    }

    public List<ManagerStatus> getAll() {
        return repo.findAll()
                .stream().filter(st -> !st.isDisabled())
                .collect(Collectors.toList());
    }
}
