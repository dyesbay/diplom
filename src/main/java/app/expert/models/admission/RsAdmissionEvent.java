package app.expert.models.admission;

import app.base.utils.DateUtils;
import app.expert.db.admission_event.AdmissionEvent;
import app.expert.db.manager.Manager;
import app.expert.services.AdmissionEventTypeService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class RsAdmissionEvent {

    private Long id;
    private Long initiator;
    private String type;
    private String created;
    private Long admission;
    private String template;
    private Map<String, Object> body;

    public static RsAdmissionEvent get(AdmissionEvent event, AdmissionEventTypeService admEventTypeServ, Manager manager) {
        return RsAdmissionEvent.builder()
                .id(event.getId())
                .created(formatDate(event.getCreated()))
                .body(body(event))
                .initiator(manager.getId())
                .type(event.getType().getKey())
                .admission(event.getAdmission())
                .template(admEventTypeServ.get(event.getType().getKey()).getTemplate())
                .build();
    }

    private static Map<String, Object> body(AdmissionEvent event) {
        return event.getBody();
    }

    private static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_DATE_TIME);
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return formatter.format(date);
    }

}
