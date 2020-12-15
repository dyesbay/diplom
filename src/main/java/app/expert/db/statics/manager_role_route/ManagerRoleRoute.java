package app.expert.db.statics.manager_role_route;

import app.base.db.GEntity;
import app.expert.db.statics.manager_role.ManagerRole;
import app.expert.db.statics.route.Route;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "manager_role_routes")
public class ManagerRoleRoute extends GEntity<ManagerRoleRouteKey> {
    
    @EmbeddedId
    private ManagerRoleRouteKey key;
    
    @ManyToOne
    @MapsId("code")
    @JoinColumn(name = "manager_role")
    private ManagerRole managerRole;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "route")
    private Route route;
}
