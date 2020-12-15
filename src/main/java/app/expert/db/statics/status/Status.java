package app.expert.db.statics.status;

import app.base.db.GEntity;
import app.expert.constants.State;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "status")
public class Status extends GEntity<String> {

    @Id
    private String code;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private State state;

    private String routeType;

    private String color;

    private Long expiredDaysNum;

    private String expiredStatus;

    private Long lifeSpan;

    private String lifeSpanStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date disabledOn;

    @Override
    public boolean isDisabled() {
        return disabledOn != null;
    }
}
