package app.expert.db.request;

import app.base.objects.GObject;
import app.expert.db.admission.CommunicationMethod;
import app.expert.validation.GPhone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantContacts extends GObject {

    private String firstName;

    private String lastName;

    private String middleName;

    private String clientType;

    @GPhone
    private String phone;

    private CommunicationMethod commType;

    @Email
    private String email;

    public void setPhone(String phone) {
        this.phone = phone
                .replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "");
        if (phone.startsWith("8")) {
            this.phone = phone.replaceFirst("8", "+7");
        }
    }
}
