package app.expert.services;

import app.base.constants.GErrors;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GException;
import app.base.utils.ObjectUtils;
import app.expert.db.statics.event_types.EventType;
import app.expert.db.statics.event_types.EventTypeCache;
import app.expert.db.statics.event_types.EventTypeRepository;
import app.expert.models.RqEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventTypeService {
    
    private final EventTypeCache cache;
    private final EventTypeRepository repo;
    
    public EventType get(String code) throws GException {
        return cache.find(code);
    }
    
    public EventType add(RqEventType rq) throws GException {
        checkInput(rq);
        cache.checkIfNotExists(rq.getCode());
        return cache.save(EventType.builder()
                .code(rq.getCode())
                .name(rq.getName())
                .description(rq.getDescription())
                .template(rq.getTemplate())
                .build());
    }

    public EventType edit(RqEventType rq) throws GException {
        checkInput(rq);
        EventType type = cache.find(rq.getCode());
        type.setName(rq.getName());
        type.setDescription(rq.getDescription());
        type.setTemplate(rq.getTemplate());
        return cache.save(type);
    }
    
    public void delete(String code) throws GException {
        EventType type = cache.find(code);
        type.setDisabledOn(new Date());
        cache.save(type);
    }
    
    public List<EventType> getAll() {
        return repo.findAll()
                .stream()
                .filter(type -> {return !type.isDisabled();})
                .collect(Collectors.toList());
    }
    
    private void checkInput(RqEventType rq)  throws GException {
        if (ObjectUtils.isBlank(rq.getCode())) throw new GBadRequest(GErrors.BAD_REQUEST);
        if (ObjectUtils.isBlank(rq.getName())) throw new GBadRequest(GErrors.BAD_REQUEST);
        if (ObjectUtils.isBlank(rq.getDescription())) throw new GBadRequest(GErrors.BAD_REQUEST);
        if (ObjectUtils.isNull(rq.getTemplate())) throw new GBadRequest(GErrors.BAD_REQUEST);
    }
}
