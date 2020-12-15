package app.expert.db.statics.request_field_values;

import app.base.db.GEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "request_field_values")
public class RequestFieldValue extends GEntity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long field;
    private String value;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date disabledOn;
    
    @Override
    public boolean isDisabled() {
        return disabledOn != null;
    }
}
