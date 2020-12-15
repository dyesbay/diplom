package app.expert.db.statics.sub_subject;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sub_subjects")
public class SubSubject extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Long maxExecutionTime;
    private Long subject;
    private Date disabledOn;

    @Override
    public boolean isDisabled() {
        return disabledOn != null;
    }
}
