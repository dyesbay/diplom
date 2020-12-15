package app.expert.db.call;

import app.base.db.GEntity;
import app.base.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "calls")
public class Call extends GEntity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;

    private String stationId;

    private String agentId;

    private String phone;

    private Long manager;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "started_on")
    private Date started;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ended_on")
    private Date ended;

    @JsonFormat(pattern = DateUtils.HUMAN_TIME)
    private Date onHold;

    private boolean incoming;

    public Date getOnHold(){
        try {
            return getFormatter().parse(getFormatter().format(this.onHold));
        }catch (Exception e){
            return null;
        }
    }

    private SimpleDateFormat getFormatter(){
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_TIME);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter;
    }
}
