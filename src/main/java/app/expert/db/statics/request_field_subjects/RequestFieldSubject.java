package app.expert.db.statics.request_field_subjects;

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
@Table(name = "request_field_subjects")
@IdClass(RqFieldSubjectKey.class)
public class RequestFieldSubject extends GEntity<RqFieldSubjectKey> {

    @Id
    private Long subject;
    @Id
    @Column(name = "request_field")
    private Long requestField;

    private boolean required;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @Column(name = "disabled_on")
    private Date disabled;

    @Override
    public boolean isDisabled() {
        return disabled != null;
    }
}
