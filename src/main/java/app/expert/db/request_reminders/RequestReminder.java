package app.expert.db.request_reminders;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "request_reminders")
public class RequestReminder extends GEntity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_on")
    private Date created;

    @Column(name = "signal_on")
    private Date signal;

    @Column(name = "disabled_on")
    private Date disabled;

    private Boolean checked;

    private String body;

    private Long request;

    private String title;
}
