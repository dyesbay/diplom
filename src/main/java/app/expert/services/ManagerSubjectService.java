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
import app.expert.db.statics.manager_subject.ManagerSubjectRepository;
import app.expert.db.statics.subjects.SubjectCache;
import app.expert.models.manager.RSManager;
import app.expert.models.manager.RsManagerSubject;
import app.expert.models.subject.RsSubject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerSubjectService {

    private final SubjectService subjectService;
    private final ManagerCache managerCache;
    private final SubjectCache subjectCache;
    private final ManagerSubjectCache managerSubjectCache;
    private final ManagerSubjectRepository repository;
    private final GContextService contextService;

    public List<RsManagerSubject> setSubjects(Long manager, List<Long> subjects) throws GException {

        //убедимся что менеджер существует
        managerCache.find(manager);

        // убедимся что все тематики из списка существуют,
        // в случае ошибки метод выбросит исключение
        for (Long subject : subjects) {
            subjectCache.find(subject);
        }

        List<RsManagerSubject> managerSubjects = new ArrayList<>(subjects.size());
        for (Long subject : subjects) {

            //  если записи нет то создадим новую
            try {
                managerSubjectCache.find(ManagerSubjectKey.builder().manager(manager).subject(subject).build());
            } catch (GNotFound e) {
                // создать новую запись в таблице manager_subject
                managerSubjectCache.save(ManagerSubject.builder()
                        .manager(manager)
                        .subject(subject)
                        .build());
            }

            // добавить в response
            managerSubjects.add(RsManagerSubject.builder()
                    .manager(manager)
                    .subject(subjectService.get(subject))
                    .build());
        }
        return managerSubjects;
    }

    public List<Long> getAllManagersBySubject(Long subject) {
        return repository.findAllBySubject(subject).stream()
                .map(ManagerSubject::getManager).collect(Collectors.toList());
    }

    /**
     * метод в котором исключаются сотрудники которые могут обрабатывать тематику
     * @param subject - айди тематики
     * @param managers - сотрудники которых убираем
     * @return - респонс модель тематики
     */
    public RsSubject excludeManagersForSubject(Long subject, List<Long> managers) throws GNotAllowed, GNotFound {

        subjectCache.find(subject);

        for (Long manager : managers) {
            managerCache.find(manager);
        }

        for (Long manager : managers) {
            try {

                ManagerSubjectKey ms = ManagerSubjectKey.builder().manager(manager).subject(subject).build();
                managerSubjectCache.delete(ms);
            } catch (GNotFound ignore) {

            }
        }
        return subjectService.getWithManagers(subject);
    }

    public List<RSManager> getExcludedManagers(Long subject) throws GNotAllowed, GNotFound {

        subjectCache.find(subject);
        List<Manager> allManagers = managerCache.getAll();
        List<Long> managersForSubject = getAllManagersBySubject(subject);
        if (managersForSubject.size() == 0) return null;
        List<RSManager> result = new ArrayList<>();
        for (Manager manager : allManagers) {
            if (!managersForSubject.contains(manager.getId())) {
                result.add(RSManager.getOnlyFullName(manager));
            }
        }
        return result;
    }
}
