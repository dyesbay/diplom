package app.expert.db.statics.region;

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
@Table(name = "regions")
public class Region extends GEntity<Long> {

    @Id
    private Long id;
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date disabledOn;
    
    @Override
    public boolean isDisabled() {
        return disabledOn != null;
    }
}
