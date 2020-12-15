package app.expert.db.statics.request_field_values;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import app.expert.models.request.RsRequestFieldValue;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestFieldValueCache extends GRedisCache<Long, RequestFieldValue, RequestFieldValueRepository> {

    public RequestFieldValueCache(RequestFieldValueRepository repository) {
        super(repository, RequestFieldValue.class);
    }

    public List<RsRequestFieldValue> findByRequestField (Long id){
        return getRepository().findByField(id).stream()
                .filter((s) -> !s.isDisabled())
                .map(RsRequestFieldValue::get)
                .collect(Collectors.toList());
    }
    
    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.REQUEST_FIELD_VALUE_ALREADY_EXIST;
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.REQUEST_FIELD_VALUE_NOT_FOUND;
    }
}
