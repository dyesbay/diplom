package app.expert.models.managers_notifications;

import app.base.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class RqManagerNotificationFilter {

    private String role;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE_TIME, timezone = "Europe/Moscow")
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE_TIME, timezone = "Europe/Moscow")
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;
}
