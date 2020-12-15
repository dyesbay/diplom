package app.expert.models;

import app.base.exceptions.GException;
import app.expert.db.manager.Manager;
import app.expert.db.statics.manager_role.ManagerRoleCache;
import app.expert.models.manager.RSManager;
import app.expert.models.manager.RSRole;
import lombok.*;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RsAuth {

    private String token;
    private RSManager manager;
    private UUID uuid;

    public static RsAuth get(Manager manager,
                             ManagerRoleCache roleCache,
                             String token,
                             String statusCode,
                             String statusName, UUID uuid) throws GException {
        return builder()
                .manager(RSManager.get(manager, RSRole.get(roleCache.find(manager.getRole())), statusCode, statusName))
                .token(token)
                .uuid(uuid)
                .build();
    }
}
