package app.expert.models.department_address_book;

import app.expert.validation.GPhone;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class RqEditDepartmentAddress {

    @NotNull
    private Long id;

    private String name;

    private String city;

    private String region;

    private String address;

    @GPhone
    private String phone;

    @Email
    private String email;

    private String contact;
}
