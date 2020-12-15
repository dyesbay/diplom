package app.expert.models.request;

import app.base.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RqRequestReminder {

    @JsonFormat(pattern= DateUtils.HUMAN_DATE_TIME, timezone = "Europe/Moscow")
    private Date signal;

    @NotNull
    private String body;

    @NotNull
    private Long request;

    private String title;

}
