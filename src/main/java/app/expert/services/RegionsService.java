package app.expert.services;

import app.base.constants.GErrors;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GException;
import app.base.utils.ObjectUtils;
import app.expert.db.statics.region.Region;
import app.expert.db.statics.region.RegionsCache;
import app.expert.db.statics.region.RegionsRepository;
import app.expert.db.statics.region_phone_numbers.RegionPhoneNumberCache;
import app.expert.models.region.RegionFilter;
import app.expert.validation.GPhoneParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionsService {

    private final RegionsCache cache;
    private final RegionsRepository repo;
    private final RegionPhoneNumberCache regionPhoneNumberCache;

    public Region find(Long id) throws GException {
        return cache.find(id);
    }

    public Region add(String name) throws GException {
        if (ObjectUtils.isBlank(name)) throw new GBadRequest(GErrors.BAD_REQUEST);
        return cache.save(Region.builder()
                .name(name)
                .build());
    }

    public Region edit(Long id, String name) throws GException {
        if (ObjectUtils.isBlank(name)) throw new GBadRequest(GErrors.BAD_REQUEST);
        Region region = cache.find(id);
        region.setName(name);
        return cache.save(region);
    }
    
    public void delete(Long id) throws GException {
        Region region = cache.find(id);
        region.setDisabledOn(new Date());
        cache.save(region);
    }

    private List<Region> getAll() {
        return repo.findAll()
                .stream()
                .filter(reg -> !reg.isDisabled())
                .collect(Collectors.toList());
    }

    /**
     * Метод ищет регион в зависимости от типа телефона(мобильный, стационарный).
     * @param number - номер телефона в формате +7(xxx)xxx-xx-xx или 8(ххх)-ххх-хх-хх
     * @return Region
     */
    public Region getRegionByPhone(String number) throws GException {
        return cache.find(regionPhoneNumberCache.findByPhone(GPhoneParser.parsePhone(number)).getRegion());
    }

    public Page<Region> getFilteredPagingList(RegionFilter filter) {
        List<Region> regions = getAll();
        return cache.getRepository().findAll(filter, new PageRequest(filter.getPage(), filter.getSize()));
    }
}
