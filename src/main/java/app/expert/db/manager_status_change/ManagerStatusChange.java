package app.expert.db.manager_status_change;

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
@Table(name = "manager_status_change")
public class ManagerStatusChange extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long manager;

    private String status;

    private Date date;

    @Column(name = "start_time")
    private Date start;

    @Column(name = "end_time")
    private Date end;

    public static ManagerStatusChange get(Date currentDate, Long manager, String status, Date start) {
        return ManagerStatusChange.builder()
                .date(currentDate)
                .manager(manager)
                .status(status)
                .start(start)
                .build();
    }
}
