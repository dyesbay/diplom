package app.expert.db.statics.admission_event_type;

import app.base.db.GEntity;
import lombok.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "admission_event_types")
public class AdmissionEventType extends GEntity<String> {

    @Id
    private String code;
    private String name;
    private String template;
}
