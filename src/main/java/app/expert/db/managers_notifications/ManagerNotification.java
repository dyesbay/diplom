package app.expert.db.managers_notifications;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "managers_notifications")
public class ManagerNotification extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "notification_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notification;

    @Column(name = "disabled_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date disabled;

    private String body;

    private String title;

    @Override
    public boolean isDisabled() {
        return disabled != null;
    }
}
