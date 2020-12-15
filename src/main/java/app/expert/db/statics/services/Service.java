package app.expert.db.statics.services;

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
@Table(name = "services")
public class Service extends GEntity<String> {
    
    @Id
    private String code;
    private String name;
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date disabledOn;
    
    @Override
    public boolean isDisabled() {
        return disabledOn != null; 
    }
}
