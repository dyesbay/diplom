package app.expert.services;

import app.base.constants.GErrors;
import app.base.exceptions.GAlreadyExists;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.utils.ObjectUtils;
import app.expert.db.statics.status.Status;
import app.expert.db.statics.status.StatusCache;
import app.expert.db.statics.status.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusCache cache;
    private final StatusRepository repo;
    
    public Status get(String code) throws GNotFound, GNotAllowed {
        return cache.find(code);
    }

    public Status add(String code, String name, String description) throws GNotFound, GNotAllowed, GAlreadyExists {
        cache.checkIfNotExists(code);
        return cache.save(Status
                .builder()
                .code(code)
                .description(description)
                .name(name)
                .build());
    }

    public Status edit(String code, String name, String description) throws GNotFound, GNotAllowed, GBadRequest {
        if (ObjectUtils.isBlank(code)) throw new GBadRequest(GErrors.BAD_REQUEST);
        Status status = cache.find(code);
        status.setName(name);
        status.setDescription(description);
        return cache.save(status);
    }
    
    public void delete(String code) throws GNotFound, GNotAllowed {
        Status status = cache.find(code);
        status.setDisabledOn(new Date());
        cache.save(status);
    }

    public List<Status> getAll() {
        return cache.getAll();
    }
}
