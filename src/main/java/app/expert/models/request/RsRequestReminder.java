package app.expert.models.request;

import app.base.utils.DateUtils;
import app.expert.db.request_reminders.RequestReminder;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsRequestReminder {

    private Long id;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE_TIME, timezone = "Europe/Moscow")
    private Date signal;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE_TIME, timezone = "Europe/Moscow")
    private Date created;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE_TIME, timezone = "Europe/Moscow")
    private Date disabled;

    private Boolean checked;

    private String body;

    private Long request;

    private String title;


    public static RsRequestReminder getFromEntity(RequestReminder requestReminder) {
        return RsRequestReminder.builder()
                .body(requestReminder.getBody())
                .id(requestReminder.getId())
                .checked(requestReminder.getChecked())
                .disabled(requestReminder.getDisabled())
                .created(requestReminder.getCreated())
                .signal(requestReminder.getSignal())
                .request(requestReminder.getRequest())
                .title(requestReminder.getTitle())
                .build();
    }
}
