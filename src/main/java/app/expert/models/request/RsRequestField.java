package app.expert.models.request;

import app.expert.constants.InputType;
import app.expert.db.statics.request_field_sub_subjects.RequestFieldSubSubject;
import app.expert.db.statics.request_field_subjects.RequestFieldSubject;
import app.expert.db.statics.request_field_values.RequestFieldValueCache;
import app.expert.db.statics.request_fields.RequestField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class RsRequestField {

    private Long id;
    private String name;
    private InputType type;
    private Boolean required;
    private List<RsRequestFieldValue> data;

    public static RsRequestField get(RequestField rf, RequestFieldValueCache valCache) {
        return RsRequestField.builder()
                .id(rf.getId())
                .name(rf.getName())
                .type(rf.getInputType())
                .data(valCache.findByRequestField(rf.getId()))
                .build();
    }

    public static RsRequestField get(RequestField rf, RequestFieldSubSubject rqFieldSubjectClone, RequestFieldValueCache valCache) {
        return RsRequestField.builder()
                .id(rf.getId())
                .name(rf.getName())
                .type(rf.getInputType())
                .data(valCache.findByRequestField(rf.getId()))
                .required(rqFieldSubjectClone.isRequired())
                .build();
    }

    public static RsRequestField get(RequestField rf, RequestFieldSubject rqFieldSubject, RequestFieldValueCache valCache) {
        return RsRequestField.builder()
                .id(rf.getId())
                .name(rf.getName())
                .type(rf.getInputType())
                .data(valCache.findByRequestField(rf.getId()))
                .required(rqFieldSubject.isRequired())
                .build();
    }
}
