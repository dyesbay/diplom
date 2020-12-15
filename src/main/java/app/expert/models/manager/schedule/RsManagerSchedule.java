package app.expert.models.manager.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.Map;

import static app.base.utils.DateUtils.HUMAN_DATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RsManagerSchedule {

    private Long manager;

    private Boolean isDismissed;

    @JsonFormat(pattern = HUMAN_DATE, timezone = "Europe/Moscow")
    private Date dateOfDismissal;

    private Boolean shouldShow;

    private String fullName;

    private Map<String, RsScheduleDay> days;
}
