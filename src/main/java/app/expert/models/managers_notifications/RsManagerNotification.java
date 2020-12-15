package app.expert.models.managers_notifications;

import app.base.utils.DateUtils;
import app.expert.db.managers_notifications.ManagerNotification;
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
public class RsManagerNotification {

    private Long id;

    private String role;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE_TIME, timezone = "Europe/Moscow")
    private Date created;

    @JsonFormat(pattern = DateUtils.HUMAN_DATE_TIME, timezone = "Europe/Moscow")
    private Date notification;

    private String text;

    private String title;

    public static RsManagerNotification get(ManagerNotification notification) {
        return RsManagerNotification.builder()
                .id(notification.getId())
                .role(notification.getRole())
                .created(notification.getCreated())
                .text(notification.getBody())
                .notification(notification.getNotification())
                .title(notification.getTitle())
                .build();
    }
}
