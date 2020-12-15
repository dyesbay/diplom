package app.expert.db.statics.request_field_sub_subjects;

import app.base.db.GEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "request_field_sub_subjects")
@IdClass(RequestFieldSubSubjectKey.class)
public class RequestFieldSubSubject extends GEntity<RequestFieldSubSubjectKey> {

    @Id
    private Long subSubject;
    @Id
    private Long requestField;
    private boolean required;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date disabledOn;

    @Override
    public boolean isDisabled() {
        return disabledOn != null;
    }
}
