package app.expert.db.statics.manager_role_route;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Embeddable
public class ManagerRoleRouteKey implements Serializable {
    
    @Column(name = "manager_role")
    private String managerCode;
    @Column(name = "route")
    private Long routeId;
}
