package app.expert.models.client;

import app.expert.db.admission.CommunicationMethod;
import app.expert.db.client.Client;
import app.expert.validation.GPhone;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RqClient {
    
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String middleName;

    @NotNull
    private CommunicationMethod communicationType;

    @NotNull
    private Long region;

    @NotBlank
    private String clientType;

    @Email(message = "Неправильный формат email")
    private String email;

    private String contact;

    @GPhone
    private String phone;

    private String company;

    private String commentary;

    public Client getClient() {
        return Client
                .builder()
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .middleName(this.getMiddleName())
                .contact(this.getContact())
                .communicationType(this.getCommunicationType())
                .commentary(this.getCommentary())
                .email(this.getEmail().toLowerCase())
                .phone(this.getPhone())
                .company(this.getCompany())
                .build();
    }
}
