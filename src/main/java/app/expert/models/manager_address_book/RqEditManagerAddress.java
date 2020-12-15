package app.expert.models.manager_address_book;

import app.expert.validation.GPhone;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class RqEditManagerAddress {

    @NotNull
    private Long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String position;

    @GPhone
    private String phone;

    @Email
    private String email;
}
