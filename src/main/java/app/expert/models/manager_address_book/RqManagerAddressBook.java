package app.expert.models.manager_address_book;

import app.expert.validation.GPhone;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class RqManagerAddressBook {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String middleName;

    @NotBlank
    private String position;

    @GPhone
    private String phone;

    @Email
    private String email;
}
