package app.expert.models.subject;

import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.db.statics.subjects.Subject;
import app.expert.models.manager.RSManager;
import app.expert.models.request.RsRequestField;
import app.expert.models.sub_subject.RsSubSubject;
import app.expert.services.RequestFieldService;
import app.expert.services.SubSubjectService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class RsSubject {

    private Long id;
    private String name;
    private String description;
    private List<RsRequestField> requestFields;
    private List<RsSubSubject> subSubjects;

    private List<RSManager> managersAllowedToWorkWith;
    private List<RSManager> managersNotAllowedToWorkWith;

    private Long maxExecutionTime;
    private boolean complaint;

    public static RsSubject get(Subject sub, RequestFieldService rqFieldService) throws GNotFound, GNotAllowed {
        return RsSubject
                .builder()
                .id(sub.getId())
                .maxExecutionTime(sub.getMaxExecutionTime())
                .name(sub.getName())
                .requestFields(rqFieldService.getBySubject(sub.getId()))
                .description(sub.getDescription())
                .complaint(sub.isComplaint())
                .build();
    }

    public static RsSubject get(Subject sub, SubSubjectService subSubService) throws GException {
        return RsSubject
                .builder()
                .id(sub.getId())
                .maxExecutionTime(sub.getMaxExecutionTime())
                .name(sub.getName())
                .description(sub.getDescription())
                .subSubjects(subSubService.getBySubject(sub.getId()))
                .complaint(sub.isComplaint())
                .build();
    }

    public static RsSubject get(Subject sub, SubSubjectService subSubService, RequestFieldService rqFieldSer) throws GException {
        return RsSubject
                .builder()
                .id(sub.getId())
                .maxExecutionTime(sub.getMaxExecutionTime())
                .name(sub.getName())
                .description(sub.getDescription())
                .subSubjects(subSubService.getBySubject(sub.getId()))
                .requestFields(rqFieldSer.getBySubject(sub.getId()))
                .complaint(sub.isComplaint())
                .build();
    }

    public static RsSubject get(Subject sub, RsSubSubject subSubject, RequestFieldService rqFieldSer) throws GNotFound, GNotAllowed {
        return RsSubject
                .builder()
                .id(sub.getId())
                .maxExecutionTime(sub.getMaxExecutionTime())
                .name(sub.getName())
                .description(sub.getDescription())
                .requestFields(rqFieldSer.getBySubSubject(subSubject.getId()))
                .complaint(sub.isComplaint())
                .build();
    }

    public static RsSubject getWithManagers(Subject subject, List<RSManager> managersAllowedToWorkWith, List<RSManager> managersNotAllowedToWorkWith) {
        return RsSubject.builder()
                .id(subject.getId())
                .name(subject.getName())
                .managersAllowedToWorkWith(managersAllowedToWorkWith)
                .managersNotAllowedToWorkWith(managersNotAllowedToWorkWith)
                .build();
    }

    public static RsSubject getWithManagers(Subject sub, RequestFieldService rqFieldService, List<RSManager> managersAllowedToWorkWith,
                                            List<RSManager> managersNotAllowedToWorkWith) throws GNotFound, GNotAllowed {
        return RsSubject.builder()
                .id(sub.getId())
                .maxExecutionTime(sub.getMaxExecutionTime())
                .name(sub.getName())
                .description(sub.getDescription())
                .requestFields(rqFieldService.getBySubject(sub.getId()))
                .complaint(sub.isComplaint())
                .managersAllowedToWorkWith(managersAllowedToWorkWith)
                .managersNotAllowedToWorkWith(managersNotAllowedToWorkWith)
                .build();
    }
}
