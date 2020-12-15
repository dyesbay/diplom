package app.expert.db.statics.integrations_configs;

import app.base.db.GEntity;
import app.expert.models.RqIntegrationConfig;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "integrations_configs")
public class IntegrationConfig extends GEntity<String> {

    @Id
    private String code;

    private String server;

    private String port;

    private String username;

    private String key;

    private String serviceName;

    private String passwordHash;

    public static IntegrationConfig getFromRequest(RqIntegrationConfig request, String code) {
        return IntegrationConfig.builder()
                .key(request.getKey())
                .server(request.getServer())
                .port(request.getPort())
                .serviceName(request.getServiceName())
                .username(request.getUsername())
                .code(code)
                .build();
    }
}
