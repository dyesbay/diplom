package app.expert.models.manager.schedule;

import app.base.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class RqEditManagerSchedule {

    @NotNull
    @JsonFormat(pattern= DateUtils.HUMAN_DATE, timezone = "Europe/Moscow")
    private Date from;

    @NotNull
    @JsonFormat(pattern= DateUtils.HUMAN_DATE, timezone = "Europe/Moscow")
    private Date to;

    @NotNull
    private Long managerId;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE, timezone = "Europe/Moscow")
    private List<Date> workdays;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE, timezone = "Europe/Moscow")
    private List<Date> weekends;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE, timezone = "Europe/Moscow")
    private List<Date> sickLeave;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE, timezone = "Europe/Moscow")
    private List<Date> vacation;
}
