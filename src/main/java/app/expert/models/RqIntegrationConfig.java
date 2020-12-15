package app.expert.models;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


@Data
public class RqIntegrationConfig {

    @NotBlank
    private String server;
    
    private String port;
    
    private String username;
    
    private String key;

    @NotBlank
    private String serviceName;
}
