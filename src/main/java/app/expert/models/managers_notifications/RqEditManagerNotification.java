package app.expert.models.managers_notifications;

import app.base.utils.DateUtils;
import app.expert.validation.StringListValidation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
public class RqEditManagerNotification {

    @NotNull
    private Long id;

    @StringListValidation
    private List<String> roles;

    @NotBlank
    private String text;

    private String title;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE_TIME, timezone = "Europe/Moscow")
    private Date notification;
}
