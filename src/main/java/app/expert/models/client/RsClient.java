package app.expert.models.client;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.db.admission.CommunicationMethod;
import app.expert.db.client.Client;
import app.expert.db.statics.client_type.ClientType;
import app.expert.db.statics.client_type.ClientTypeCache;
import app.expert.db.statics.region.Region;
import app.expert.db.statics.region.RegionsCache;
import lombok.*;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Component
public class RsClient {
    
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private CommunicationMethod communicationType;
    private String company; 
    private String commentary;
    private String contact;
    private Region region; 
    private ClientType clientType;
    
    public static RsClient get(Client client, RegionsCache regCache, ClientTypeCache typeCache) throws GNotFound, GNotAllowed {
        return RsClient.builder()
                .id(client.getId())
                .clientType(clientType(typeCache, client.getClientType()))
                .region(region(regCache, client.getRegion()))
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .commentary(client.getCommentary())
                .email(client.getEmail())
                .communicationType(client.getCommunicationType())
                .company(client.getCompany())
                .phone(client.getPhone()).contact(client.getContact())
                .build();
    }

    private static ClientType clientType(ClientTypeCache typeCache, String clientType ){
        try {
            return typeCache.find(clientType);
        } catch (GNotFound | GNotAllowed e) {
            return null;
        }
    }

    private static Region region (RegionsCache regCache, Long region ){
        try {
            return regCache.find(region);
        } catch (GNotFound | GNotAllowed e) {
            return null;
        }
    }
}
