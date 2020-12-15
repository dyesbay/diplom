package app.expert.services;

import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.services.GContextService;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.statics.manager_subject.ManagerSubject;
import app.expert.db.statics.manager_subject.ManagerSubjectCache;
import app.expert.db.statics.manager_subject.ManagerSubjectKey;
import app.expert.db.statics.request_field_sub_subjects.RqFieldSubSubjectRepository;
import app.expert.db.statics.request_field_subjects.RequestFieldSubject;
import app.expert.db.statics.request_field_subjects.RequestFieldSubjectRepository;
import app.expert.db.statics.sections.SectionCache;
import app.expert.db.statics.subjects.Subject;
import app.expert.db.statics.subjects.SubjectCache;
import app.expert.db.statics.subjects.SubjectRepository;
import app.expert.models.manager.RSManager;
import app.expert.models.subject.RqSubject;
import app.expert.models.sub_subject.RsSubSubject;
import app.expert.models.subject.RsSubject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SubjectService {
    
    private final SubjectCache cache;
    private final ManagerCache managerCache;
    private final ManagerSubjectCache managerSubjectCache;
    private final SubjectRepository repo;
    private final SectionCache sectionCache;
    private final SubSubjectService subSubjectService;
    private final RequestFieldService rqFieldSer;
    private final SubjectsAccessService subjectsAccessService;
    private final GContextService contextService;

    private final RequestFieldSubjectRepository rqFieldSubjectRepo;
    private final RqFieldSubSubjectRepository rqFieldSubjectSubSubjectRepo;
    private final RequestFieldService fieldService;

    public RsSubject get(final Long id) throws GException {
        return RsSubject.get(cache.find(id), subSubjectService, rqFieldSer);
    }

    private void setManagersForNewSubject(Long subject) throws GNotAllowed, GNotFound {

        String platform = managerCache.find(contextService.getUser()).getPlatform();

        List<Manager> managers = managerCache.getAll().stream()
                .filter(manager -> manager.getPlatform().equals(platform)).collect(Collectors.toList());

        for (Manager manager : managers) {

            //  если записи нет то создадим новую
            try {
                managerSubjectCache.find(ManagerSubjectKey.builder().manager(manager.getId()).subject(subject).build());
            } catch (GNotFound e) {
                // создать новую запись в таблице manager_subject
                managerSubjectCache.save(ManagerSubject.builder()
                        .manager(manager.getId())
                        .subject(subject)
                        .build());
            }
        }
    }

    public RsSubject getWithManagers(final Long id) throws GNotFound, GNotAllowed {

        String platform = managerCache.find(contextService.getUser()).getPlatform();

        List<Manager> allManagersFromPlatform = managerCache.getAll().stream()
                .filter(manager -> manager.getPlatform().equals(platform)).collect(Collectors.toList());

        List<RSManager> managersAllowedToWorkWith = new ArrayList<>();
        List<RSManager> managersNotAllowedToWorkWith = new ArrayList<>();

        for (Manager manager : allManagersFromPlatform) {
            try {
                managerSubjectCache.find(ManagerSubjectKey.builder().manager(manager.getId()).subject(id).build());
                managersAllowedToWorkWith.add(RSManager.builder().fullName(manager.getFullName()).build());
            } catch (GNotFound e) {
                managersNotAllowedToWorkWith.add(RSManager.builder().fullName(manager.getFullName()).build());
            }
        }
        return RsSubject.getWithManagers(cache.find(id), rqFieldSer, managersAllowedToWorkWith, managersNotAllowedToWorkWith);
    }

    public RsSubject get(final Long subject, final Long subSubject) throws GException {
        if(subSubject != null && !subSubjectService.getBySubject(subject).isEmpty()) {
            return RsSubject.get(cache.find(subject), subSubjectService.get(subSubject), rqFieldSer);
        }
        return RsSubject.get(cache.find(subject), subSubjectService, rqFieldSer);
    }
    
    public RsSubject add(final RqSubject rq) throws GException {

        checkSection(rq.getSection());
        if(rq.getMaxExecutionTime() == null) {
            rq.setMaxExecutionTime(0L);
        }
        Subject subject = cache.save(rq.getSubject());
        subjectsAccessService.addDefaultAccessConfig(subject.getId());
        setManagersForNewSubject(subject.getId());
        return RsSubject.get(subject, rqFieldSer);
    }
    
    public RsSubject update(Long id, final RqSubject rq) throws GException {
        checkSection(rq.getSection());
        if(rq.getMaxExecutionTime() == null) {
            rq.setMaxExecutionTime(0L);
        }
        cache.find(id);
        return RsSubject.get(cache.save(rq.getSubject()), rqFieldSer);
    }
    
    public void delete(final Long id) throws GException {
        for(RequestFieldSubject rqFieldSub : rqFieldSubjectRepo.findBySubject(id))
            fieldService.delete(rqFieldSub.getSubject());
        for (RsSubSubject subSubject :subSubjectService.getBySubject(id))
            subSubjectService.delete(subSubject.getId());
        cache.delete(id);
    }

    public List<RsSubject> getBySection(Long id) throws GException {
        List<Subject> notDisabledOnly = repo.findBySection(id).stream()
                .filter(s -> !s.isDisabled())
                .collect(Collectors.toList());
        List<RsSubject> result = new LinkedList<>();
        for(Subject sub : notDisabledOnly) {
            result.add(RsSubject.get(sub, subSubjectService));
        }
        return result;
    }

    private void checkSection(final Long id) throws GException {
        sectionCache.find(id);
    }

    public List<RsSubject> getAll() throws GException {
        List<RsSubject> result = new ArrayList<>();
        for(Subject subject : repo.findAll()) {
            result.add(RsSubject.get(subject, subSubjectService));
        }
        return result;
    }
}
