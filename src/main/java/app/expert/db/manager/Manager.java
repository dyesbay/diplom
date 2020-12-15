package app.expert.db.manager;

import app.base.db.GEntity;
import app.base.utils.ObjectUtils;
import app.expert.models.manager.RQCreateManager;
import app.expert.models.manager.RQEditManager;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "managers")
public class Manager extends GEntity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String email;

    private String role;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registered_on")
    private Date registered;

    private String platform;

    private String username;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "disabled_on")
    private Date disabled;

    @Column(name = "agent_id")
    private String agentId;

    @Column(name = "agent_password_hash")
    private String agentPasswordHash;

    @Column(name = "station_id")
    private String stationId;

    private String workDayStatus;

    private Date dateOfDismissal;
    
    @Override
    public boolean isDisabled() {
        return disabled != null;
    }

    public static Manager createManager(RQCreateManager managerInfo) {
        return Manager.builder()
                .username(managerInfo.getUsername())
                .role(managerInfo.getRoleCode())
                .email(managerInfo.getEmail())
                .firstName(managerInfo.getFirstName())
                .lastName(managerInfo.getLastName())
                .middleName(managerInfo.getMiddleName())
                .platform(managerInfo.getPlatform())
                .registered(new Date())
                .build();
    }

    public Manager update(RQEditManager managerInfo) {
        if (!ObjectUtils.isBlank(managerInfo.getFirstName())) {
            this.setFirstName(managerInfo.getFirstName());
        }
        if (!ObjectUtils.isBlank(managerInfo.getMiddleName())) {
            this.setMiddleName(managerInfo.getMiddleName());
        }
        if (!ObjectUtils.isBlank(managerInfo.getLastName())) {
            this.setLastName(managerInfo.getLastName());
        }
        if (!ObjectUtils.isBlank(managerInfo.getEmail())) {
            this.setEmail(managerInfo.getEmail());
        }
        if (!ObjectUtils.isBlank(managerInfo.getPlatform())) {
            this.setPlatform(managerInfo.getPlatform());
        }
        return this;
    }

    public String getFullName() {
        String fullName = this.lastName + " " + this.firstName.charAt(0) + ".";
        if (!ObjectUtils.isBlank(this.middleName))
            fullName = fullName + " " + this.middleName.charAt(0) + ".";
        return fullName;
    }
}
