package app.expert.db.schedule.general_schedule;

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
@Table(name = "general_schedule")
public class GeneralSchedule extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    private String type;
}
