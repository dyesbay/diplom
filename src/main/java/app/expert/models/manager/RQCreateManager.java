package app.expert.models.manager;

import app.expert.validation.GPlatform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@NoArgsConstructor
@Component
@Data
public class RQCreateManager {

    @NotBlank
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String middleName;

    @NotBlank
    private String roleCode;

    @NotBlank
    @Email(message = "Неправильный формат email")
    private String email;

    @NotBlank
    @GPlatform
    private String platform;
}
