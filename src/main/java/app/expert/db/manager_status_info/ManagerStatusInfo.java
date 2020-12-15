package app.expert.db.manager_status_info;

import app.base.db.GEntity;
import app.base.utils.SimpleJsonConverter;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "manager_status_info")
public class ManagerStatusInfo extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long manager;

    private Date date;

    private Date modified;

    @Column(name = "current_status")
    private String currentStatus;

    @Convert(converter = SimpleJsonConverter.class)
    private Map<String, Object> info;

    public void calculateTimeForCurrentStatus() {
        for (Map.Entry<String, Object> records : this.info.entrySet()) {
            if (records.getKey().equals(this.currentStatus)) {
                Date now = new Date();
                long diff = now.getTime() - this.modified.getTime();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                long newValue = (((Double)records.getValue()).longValue()) + minutes;
                if (minutes > 0) {
                    records.setValue(newValue);
                    this.setModified(now);
                }
            }
        }
    }
}
