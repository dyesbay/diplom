package app.expert.models.admission;

import app.expert.constants.Platform;
import app.expert.db.admission.Admission;
import app.expert.db.call.Call;
import app.expert.db.manager.Manager;
import app.expert.db.statics.region.Region;
import app.expert.db.statics.subjects.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class RsAdmission {

    private Long id;
    private String managerName;
    private String managerPlatform;
    private String clientPhone;
    private String clientEmail;
    private String clientName;
    private String region;
    private String subject;
    private String result;
    private String startOn;
    private String callDuration;
    private String endOn;
    private String onHold;
    private String comment;
    private List<RsAdmissionEvent> events;

    public static RsAdmission get(Admission adm,
                                  Manager manager,
                                  Region region,
                                  Call call,
                                  List<RsAdmissionEvent> events,
                                  Subject subject) {
        return RsAdmission
                .builder()
                .id(adm.getId())
                .clientPhone(adm.getClientPhone())
                .clientEmail(adm.getClientEmail())
                .clientName(adm.getClientName())
                .managerName(manager.getFirstName() + " " + manager.getLastName())
                .managerPlatform(Platform.valueOf(manager.getPlatform()).getValue())
                .region(region != null ? region.getName() : null)
                .startOn(call.getStarted() != null ? formatDate(call.getStarted()) : null)
                .endOn(call.getEnded() != null ? formatDate(call.getEnded()) : null)
                .onHold(call.getOnHold() != null ? formatTime(call.getOnHold()) : null)
                .result(adm.getResult() != null ? adm.getResult() : null)
                .subject(subject != null ? subject.getName() : null)
                .comment(adm.getComment() != null ? adm.getComment() : null)
                .events(events)
                .callDuration(formatCallDuration(call))
                .build();
    }

    private static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        if(date != null){
            return formatter.format(date);
        }
        return null;
    }

    private static String formatTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getDefault());
        if(date != null){
            return formatter.format(date);
        }
        return null;
    }

    private static String formatCallDuration(Call call){
        if(call.getEnded() == null)
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(new Date(call.getEnded().getTime() - call.getStarted().getTime()));
    }
}
