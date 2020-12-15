package app.expert.db.statics.manager_role;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "manager_roles")
public class ManagerRole extends GEntity<String> {

    @Id
    private String code;

    private String name;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "disabled_on")
    private Date disabled;
    
    @Override
    public boolean isDisabled() {
        return this.disabled != null;
    }
}
