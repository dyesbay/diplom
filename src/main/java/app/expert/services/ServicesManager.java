package app.expert.services;

import app.base.constants.GErrors;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GException;
import app.base.utils.ObjectUtils;
import app.expert.db.statics.services.Service;
import app.expert.db.statics.services.ServiceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ServicesManager {

    private final ServiceCache cache;

    public Service get(String code) throws GException {
        return cache.find(code);
    }

    public Service add(String code, String name, String description) throws GException {
        checkInput(code, name);
        cache.checkIfNotExists(code);
        return cache.save(Service.builder().code(code)
                .name(name)
                .description(description)
                .build());
    }

    public void delete(String code) throws GException {
        Service service = cache.find(code);
        service.setDisabledOn(new Date());
        cache.save(service);
    }

    public List<Service> getAll() {
        return cache.getAll().stream().filter(serv -> {
            return !serv.isDisabled();
        }).collect(Collectors.toList());
    }

    public Service edit(String code, String name, String description) throws GException {
        checkInput(code, name);
        Service service = cache.find(code);
        service.setName(name);
        service.setDescription(description);
        return cache.save(service);
    }

    private void checkInput(String code, String name) throws GException {
        if (ObjectUtils.isBlank(code)) throw new GBadRequest(GErrors.BAD_REQUEST);
        if (ObjectUtils.isBlank(name)) throw new GBadRequest(GErrors.BAD_REQUEST);
    }
}
