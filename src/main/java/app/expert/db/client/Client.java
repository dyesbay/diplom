package app.expert.db.client;

import app.base.db.GEntity;
import app.base.utils.ObjectUtils;
import app.expert.db.admission.CommunicationMethod;
import app.expert.models.client.RqClient;
import app.expert.validation.GPhoneParser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "clients")
public class Client extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    @Enumerated(EnumType.STRING)
    private CommunicationMethod communicationType;
    private String company;
    private String commentary;
    private Long region;
    private String contact;
    private String clientType;
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    private Date disabledOn;

    @Override
    public boolean isDisabled() {
        return disabledOn != null;
    }

    public static Client createClientFromRequest(RqClient request) {
        return Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .clientType(request.getClientType())
                .region(request.getRegion())
                .email(request.getEmail())
                .phone(GPhoneParser.parsePhone(request.getPhone()))
                .commentary(request.getCommentary())
                .communicationType(request.getCommunicationType())
                .company(request.getCompany())
                .contact(request.getContact())
                .build();
    }

    public Client updateClient(RqClient request) {
        Client client = createClientFromRequest(request);
        client.setId(this.id);
        return client;
    }

    public String getFullName() {
        return ObjectUtils.toStringOrEmpty(getLastName()) + " "
                + ObjectUtils.toStringOrEmpty(getFirstName()) + " "
                + ObjectUtils.toStringOrEmpty(getMiddleName());
    }
}
