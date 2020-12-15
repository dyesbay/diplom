package app.expert.services;

import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.db.statics.request_field_values.RequestFieldValue;
import app.expert.db.statics.request_field_values.RequestFieldValueCache;
import app.expert.db.statics.request_field_values.RequestFieldValueRepository;
import app.expert.db.statics.request_fields.RequestFieldCache;
import app.expert.models.request.RsRequestFieldValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RequestFieldValueService {
       
    private final RequestFieldValueCache cache;
    private final RequestFieldCache fieldCache;
    private final RequestFieldValueRepository repo;

    public RsRequestFieldValue get(Long id) throws GNotFound, GNotAllowed {
        return RsRequestFieldValue.get(cache.find(id));
    }

    public List<RsRequestFieldValue> getByRequestField(Long id) throws GNotFound, GNotAllowed {
        fieldCache.find(id);
        List<RequestFieldValue> notDisabledOnly = repo.findByField(id)
                .stream()
                .filter(requestFieldValue -> {return !requestFieldValue.isDisabled();})
                .collect(Collectors.toList());
        List<RsRequestFieldValue> result = new LinkedList<>();
        for (RequestFieldValue rqFieldVal : notDisabledOnly) {
            result.add(get(rqFieldVal.getId()));
        }
        return result;
    }

    public RsRequestFieldValue add(String value, Long id) throws GException {
        fieldCache.find(id);
        return RsRequestFieldValue.get(cache.save(RequestFieldValue.builder()
                .field(id)
                .value(value)
                .build()));
    }

    public void delete(Long id) throws GException {
        RequestFieldValue rfv = cache.find(id);
        rfv.setDisabledOn(new Date());
        cache.save(rfv);
    }
}
