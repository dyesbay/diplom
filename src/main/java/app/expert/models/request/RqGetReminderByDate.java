package app.expert.models.request;

import app.base.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class RqGetReminderByDate {

    @JsonFormat(pattern= DateUtils.HUMAN_DATE_TIME, timezone = "Europe/Moscow")
    private Date date;
}
