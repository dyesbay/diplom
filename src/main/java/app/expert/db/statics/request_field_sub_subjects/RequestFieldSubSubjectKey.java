package app.expert.db.statics.request_field_sub_subjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Embeddable
public class RequestFieldSubSubjectKey implements Serializable {

    @Column(name = "request_field")
    private Long requestField;

    @Column(name = "sub_subject")
    private Long subSubject;
}
