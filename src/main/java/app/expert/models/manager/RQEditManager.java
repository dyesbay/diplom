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
public class RQEditManager {

    @NotBlank
    private String username;

    private String firstName;

    private String lastName;

    private String middleName;

    @Email(message = "Неправильный формат email")
    private String email;

    @GPlatform
    private String platform;

    private String role;
}
