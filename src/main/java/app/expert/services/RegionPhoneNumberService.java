package app.expert.services;

import app.base.exceptions.GAlreadyExists;
import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.models.GFilter;
import app.base.utils.DateUtils;
import app.expert.db.statics.region.Region;
import app.expert.db.statics.region.RegionsCache;
import app.expert.db.statics.region_phone_numbers.RegionPhoneNumber;
import app.expert.db.statics.region_phone_numbers.RegionPhoneNumberCache;
import app.expert.db.uploaded_files.UploadedFileRepository;
import app.expert.models.region.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequiredArgsConstructor
@Service
public class RegionPhoneNumberService {

    private final RegionPhoneNumberCache cache;
    private final RegionsCache regionCache;
    private final FileService service;
    private final UploadedFileRepository upFileRepo;

    /**
     * Общие методы для работы с ABC/DEF номерами
     */
    public Page<RsRegionPhoneNumber> getPaging(int page, int size) {
        List<Region> regions = regionCache.getAll();
        return cache.getPaging(new PageRequest(page,size)).map(s -> RsRegionPhoneNumber.get(s, regions));
    }

    public void delete(Long id) throws GNotFound {
        cache.delete(id);
    }

    /**
     * Методы для работы с ABC номерами
     */

    public Page<RsABCRegionPhoneNumber> getABCPaging(int page, int size) {
        List<Region> regions = regionCache.getAll();
        RegionPhoneNumber probe = new RegionPhoneNumber();
        probe.setDTo(0);
        probe.setDFrom(0);
        return cache.getPaging(Example.of(probe), new PageRequest(page,size))
                .map(s -> RsABCRegionPhoneNumber.get(s, regions));
    }

    public RsABCRegionPhoneNumber saveABC(RqABCRegionPhoneNumber rq) throws GAlreadyExists {
        List<Region> regions = regionCache.getAll();
        RegionPhoneNumber probe = new RegionPhoneNumber();
        probe.setDTo(0);
        probe.setDFrom(0);
        probe.setCode(rq.getCode());
        probe.setRegion(rq.getRegion());
        //Проверяем есть ли такой экземпляр в базе
        RegionPhoneNumber regPhoneNumber = cache.getRepository().findOne(Example.of(probe));
        if(regPhoneNumber != null)
            throw new GAlreadyExists(cache.getAlreadyExistsError());
        return RsABCRegionPhoneNumber.get(cache.save(rq.get()), regions);
    }

    public RsABCRegionPhoneNumber updateABC(Long id, RqABCRegionPhoneNumber rq) throws GNotAllowed, GNotFound {
        List<Region> regions = regionCache.getAll();
        RegionPhoneNumber item = cache.find(id);
        item.setCode(rq.getCode());
        item.setRegion(rq.getRegion());
        return RsABCRegionPhoneNumber.get(item, regions);
    }

    /**
     * Методы для работы с DEF номерами
     */
    public Page<RsDEFRegionPhoneNumber> getDEFPaging(int page, int size) {
        List<Region> regions = regionCache.getAll();
        GFilter<Long, RegionPhoneNumber> filter = new GFilter<Long, RegionPhoneNumber>() {
            @Override
            public List<Predicate> getPredicates(Root root, CriteriaBuilder cb) {
                List<Predicate> predicates = new LinkedList<>();
                predicates.add(cb.notEqual(root.get("dTo"), 0));
                predicates.add(cb.notEqual(root.get("dFrom"), 0));
                return predicates;
            }
        };
        Page<RegionPhoneNumber> fromBase = cache.getRepository()
                .findAll(filter, new PageRequest(page, size));
        return fromBase.map(new Converter<RegionPhoneNumber, RsDEFRegionPhoneNumber>()
                {
                    @Override
                    public RsDEFRegionPhoneNumber convert(RegionPhoneNumber source) {
                        return RsDEFRegionPhoneNumber.get(source, regions);
                    }
                });
    }

    public RsDEFRegionPhoneNumber saveDEF(RqDEFRegionPhoneNumber rq) throws GAlreadyExists {
        List<Region> regions = regionCache.getAll();
        GFilter<Long, RegionPhoneNumber> filter = new GFilter<Long, RegionPhoneNumber>() {
            @Override
            public List<Predicate> getPredicates(Root root, CriteriaBuilder cb) {
                List<Predicate> predicates = new LinkedList<>();
                predicates.add(cb.equal(root.get("dTo"), rq.getTo()));
                predicates.add(cb.equal(root.get("dFrom"), rq.getFrom()));
                predicates.add(cb.equal(root.get("code"), rq.getCode()));
                predicates.add(cb.equal(root.get("region"), rq.getRegion()));
                return predicates;
            }
        };
        RegionPhoneNumber fromBase = cache.getRepository().findOne(filter);
        if(fromBase != null) throw new GAlreadyExists(cache.getAlreadyExistsError());
        return RsDEFRegionPhoneNumber.get(cache.save(rq.get()), regions);
    }

    public RsDEFRegionPhoneNumber updateDEF(Long id, RqDEFRegionPhoneNumber rq) throws GNotAllowed, GNotFound {
        List<Region> regions = regionCache.getAll();
        RegionPhoneNumber item = cache.find(id);
        item.setCode(rq.getCode());
        item.setRegion(rq.getRegion());
        item.setDFrom(rq.getFrom());
        item.setDTo(rq.getTo());
        return RsDEFRegionPhoneNumber.get(cache.save(item), regions);
    }

    /**
     * Загрузка фалов с ABC/DEF номерами
     */
    public void uploadPhoneCodesFile(String input) throws IOException, GException {
        service.processFile(input);
    }

    public List<FileInfo> getFileInfo() {
        List<FileInfo> result = new ArrayList<>();
        List<Object[]> rows = upFileRepo.getFilesInfo();
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_DATE_TIME);
        for(Object[] row : rows) {
            String name = (String)row[0];
            String size = (String)row[1];
            Date uploaded = (Date)row[2];
            result.add(FileInfo.builder()
                    .name(name)
                    .size(size)
                    .date(formatter.format(uploaded))
                    .build());
        }
        return result;
    }

    private String formatDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_DATE_TIME);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(date);
    }
}
