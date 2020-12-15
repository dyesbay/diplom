package app.expert.models.manager;

import app.base.utils.DateUtils;
import lombok.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RsTodayManagerStat {

    private int callAmount;
    private String averageCallDuration;
    private String averageOnHold;

    public static RsTodayManagerStat get(int callAmount, long averOnHold, long averCallDuration) {
        return RsTodayManagerStat.builder()
                .callAmount(callAmount)
                .averageCallDuration(formatTime(averCallDuration))
                .averageOnHold(formatOnHold(averOnHold))
                .build();
    }

    public static RsTodayManagerStat getDefault() {
        return RsTodayManagerStat.builder()
                .callAmount(0)
                .averageCallDuration(formatMinTime())
                .averageOnHold(formatMinTime())
                .build();
    }

    private static String formatMinTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_TIME);
        formatter.setTimeZone(TimeZone.getTimeZone("GTM+00:00"));
        return formatter.format(new Date(0));
    }

    private static String formatTime(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_TIME);
        formatter.setTimeZone(TimeZone.getTimeZone("GTM+00:00"));
        return formatter.format(new Date(millis));
    }

    private static String formatOnHold(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.HUMAN_TIME);
        return formatter.format(new Date(millis));
    }
}
