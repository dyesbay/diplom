package app.expert.models.manager.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

import static app.base.utils.DateUtils.HUMAN_DATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RsScheduleDay {

    private String dayType;

    @JsonFormat(pattern = HUMAN_DATE, timezone = "Europe/Moscow")
    private Date date;

    private String color;
}
