package app.expert.models.sub_subject;

import app.base.exceptions.GException;
import app.expert.db.statics.sub_subject.SubSubject;
import app.expert.models.request.RsRequestField;
import app.expert.services.RequestFieldService;
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
public class RsSubSubject {

    private Long id;
    private String name;
    private String description;
    private Long maxExecutionTime;
    private List<RsRequestField> requestFields;

    public static RsSubSubject get(SubSubject subSubject) {
        return RsSubSubject.builder()
                .id(subSubject.getId())
                .name(subSubject.getName())
                .description(subSubject.getDescription())
                .maxExecutionTime(subSubject.getMaxExecutionTime())
                .build();
    }

    public static RsSubSubject get(SubSubject subSubject, RequestFieldService ser) throws GException {
        return RsSubSubject.builder()
                .id(subSubject.getId())
                .name(subSubject.getName())
                .description(subSubject.getDescription())
                .maxExecutionTime(subSubject.getMaxExecutionTime())
                .requestFields(ser.getBySubSubject(subSubject.getId()))
                .build();
    }
}
