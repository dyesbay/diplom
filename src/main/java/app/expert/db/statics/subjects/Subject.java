package app.expert.db.statics.subjects;

import app.base.db.GEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "subjects")
public class Subject extends GEntity<Long>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Long section;

    private Long maxExecutionTime;

    private boolean complaint;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date disabledOn;
    
    @Override
    public boolean isDisabled() {
        return disabledOn != null;
    }
}
