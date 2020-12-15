package app.expert.db.statics.request_field_subjects;

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
public class RqFieldSubjectKey implements Serializable {

    @Column(name = "request_field")
    private Long requestField;
    @Column(name = "subject")
    private Long subject;
}
