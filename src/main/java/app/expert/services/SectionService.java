package app.expert.services;

import app.base.constants.GErrors;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GException;
import app.base.utils.ObjectUtils;
import app.expert.db.statics.sections.Section;
import app.expert.db.statics.sections.SectionCache;
import app.expert.models.section.RqSection;
import app.expert.models.section.RsSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionCache cache;
    private final SubjectService subjectService;

    public RsSection add(RqSection rq) throws GException {
        if (ObjectUtils.isBlank(rq.getName())) {
            throw new GBadRequest(GErrors.BAD_REQUEST);
        }
        return RsSection.get(cache.save(rq.get()), subjectService);
    }

    public RsSection update(Long id, RqSection rq) throws GException {
        Section sec = rq.get();
        sec.setId(id);
        return RsSection.get(cache.save(sec), subjectService);
    }

    public RsSection get(Long id) throws GException {
        return RsSection.get(cache.find(id), subjectService);
    }

    public void delete(Long id) throws GException {
        Section sec = cache.find(id);
        sec.setDisabledOn(new Date());
        cache.save(sec);
    }

    public List<RsSection> getAll() throws GException {
        List<Section> notDisabledOnOnly = cache.getAll()
                .stream()
                .filter(s -> !s.isDisabled())
                .collect(Collectors.toList());
        List<RsSection> result = new LinkedList<>();
        for(Section section : notDisabledOnOnly) {
            result.add(RsSection.get(section, subjectService));
        }
        return result;
    }
}
