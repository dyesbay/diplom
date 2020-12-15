package app.expert.models.manager;

import app.expert.db.statics.manager_role.ManagerRole;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RSRole {

    private String code;

    private String name;

    private String description;

    public static RSRole get(ManagerRole role) {
        return RSRole.builder()
                .code(role.getCode())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
