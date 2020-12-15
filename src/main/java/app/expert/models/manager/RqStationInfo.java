package app.expert.models.manager;

import app.base.objects.GObject;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RqStationInfo extends GObject {

    @NotBlank
    private String username;

    private String agentId;

    private String password;

    private String stationId;
}
