package app.expert.services;

import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.db.statics.request_field_sub_subjects.RequestFieldSubSubject;
import app.expert.db.statics.request_field_sub_subjects.RqFieldSubSubjectRepository;
import app.expert.db.statics.sub_subject.SubSubject;
import app.expert.db.statics.sub_subject.SubSubjectCache;
import app.expert.db.statics.sub_subject.SubSubjectRepository;
import app.expert.db.statics.subjects.SubjectCache;
import app.expert.models.sub_subject.RqSubSubject;
import app.expert.models.sub_subject.RsSubSubject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SubSubjectService {

    private final SubSubjectRepository repo;
    private final SubSubjectCache cache;
    private final SubjectCache subCache;
    private final RequestFieldService rqFieldService;
    private final RqFieldSubSubjectRepository rqFieldSubSubjectRepo;

    public List<RsSubSubject> getBySubject(Long id) throws GException {
        subCache.find(id);
        List<SubSubject> subSubjects = repo.findBySubject(id);
        List<RsSubSubject> result = new LinkedList<>();
        for(SubSubject subSubject : subSubjects) {
            result.add(RsSubSubject.get(subSubject, rqFieldService));
        }
        return result;
    }

    public RsSubSubject add(RqSubSubject rq) throws GNotAllowed, GNotFound {
        subCache.find(rq.getSubject());
        if(rq.getMaxExecutionTime() == null)
            rq.setMaxExecutionTime(0L);
        return RsSubSubject.get(cache.save(rq.getSubSubject()));
    }

    public RsSubSubject get(Long id) throws GException {
        return RsSubSubject.get(cache.find(id), rqFieldService);
    }

    public void delete(Long id) throws GNotFound, GNotAllowed {
        for(RequestFieldSubSubject fieldSubSub :rqFieldSubSubjectRepo.findBySubSubject(id)){
            rqFieldService.delete(fieldSubSub.getRequestField());
        }
        cache.delete(id);
    }
}
