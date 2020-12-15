package app.expert.db.schedule.manager_schedule;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "managers_schedule")
public class ManagerSchedule extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long manager;

    private Date date;

    private String type;
}
