package app.expert.db.statics.manager_status;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "manager_status")
public class ManagerStatus extends GEntity<String> {

    @Id
    private String code;

    private String name;

    private String workMode;

    private String reasonCode;

    private String description;

    @Column(name = "disabled_on")
    private Date disabled;

    @Override
    public boolean isDisabled() {
        return disabled != null;
    }
}
