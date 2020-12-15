package app.expert.db.request;

import app.base.db.GEntity;
import app.base.utils.SimpleJsonConverter;
import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.db.client.Client;
import app.expert.models.request.MField;
import app.expert.models.request.RqRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "requests")
public class Request extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long assignee;

    private Long requester;

    private String status;

    @Column(name = "created_on")
    private Date created;
    @Column(name = "route_type")
    private String routeType;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private Long link;

    @Convert(converter = IncomingRequestBodyConverter.class)
    private List<MField> body;

    private Long subject;

    @Column(name = "sub_subject")
    private Long subSubject;

    private Long admission;

    @Convert(converter = SimpleJsonConverter.class)
    private Map<String, Object> applicantContacts;

    private String text;

    @JsonIgnore
    @Column(name = "disabled_on")
    private Date disabled;

    private Date expiresDate;

    private Date lifeSpanDate;

    @Column(name = "closed_on")
    private Date closed;

    @Column(name = "assigned_on")
    private Date assigned;

    @Override
    public boolean isDisabled() {
        return disabled != null;
    }

    public static Request getFromIncomingRequest(RqRequest request, Client client, Map<String, Object> clientContacts,
                                                 Long admission) {
        return Request.builder()
                .requester(client.getId())
                .body(request.getData())
                .subject(request.getSubject())
                .subSubject(request.getSubSubject())
                .applicantContacts(clientContacts)
                .status(request.getStatus())
                .channel(request.getChannel())
                .text(request.getText())
                .admission(admission)
                .created(new Date())
                .link(request.getLink())
                .build();
    }
}
