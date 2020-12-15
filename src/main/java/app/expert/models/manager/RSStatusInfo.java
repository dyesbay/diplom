package app.expert.models.manager;

import app.expert.db.manager_status_info.ManagerStatusInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RSStatusInfo {

    private String agentId;

    @JsonFormat(pattern="dd.MM.yyyy", timezone = "Europe/Moscow")
    private Date date;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Moscow")
    private Date modified;

    private String currentStatus;

    private Map<String, Object> info;

    public static RSStatusInfo get(String agentId, ManagerStatusInfo statusInfo, Date currentDate) {
        return RSStatusInfo.builder()
                .agentId(agentId)
                .date(currentDate)
                .info(statusInfo.getInfo())
                .currentStatus(statusInfo.getCurrentStatus())
                .modified(statusInfo.getModified())
                .build();
    }
}
