package app.expert.models.department_address_book;

import app.expert.validation.GPhone;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class RqDepartmentAddressBook {

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotBlank
    private String region;

    private String address;

    @GPhone
    private String phone;

    @Email
    private String email;

    private String contact;
}
