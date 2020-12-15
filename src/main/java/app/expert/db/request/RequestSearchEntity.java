package app.expert.db.request;

import app.base.db.GEntity;
import app.base.utils.SimpleJsonConverter;
import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.db.client.Client;
import app.expert.db.manager.Manager;
import app.expert.db.statics.route_type.RouteType;
import app.expert.db.statics.status.Status;
import app.expert.db.statics.subjects.Subject;
import app.expert.models.request.MField;
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
//TODO : Сделать родительский класс для RequestSearchEntity и Request
public class RequestSearchEntity extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignee", referencedColumnName = "id", insertable = false, updatable = false)
    private Manager assignee;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "requester", referencedColumnName = "id", insertable = false, updatable = false)
    private Client requester;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status", referencedColumnName = "code", insertable = false, updatable = false)
    private Status status;

    @Column(name = "created_on")
    private Date created;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "route_type", referencedColumnName = "code", insertable = false, updatable = false)
    private RouteType routeType;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private Long link;

    @Convert(converter = IncomingRequestBodyConverter.class)
    private List<MField> body;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject", referencedColumnName = "id", insertable = false, updatable = false)
    private Subject subject;

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
}
