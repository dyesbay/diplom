package app.expert.models.manager;

import app.base.objects.GObject;
import app.expert.constants.Platform;
import app.expert.db.manager.Manager;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RSManager extends GObject {

    private Long id;

    private String username;

    private String firstName;

    private String middleName;

    private String lastName;

    private String fullName;

    private String email;

    private String platform;

    private RSRole role;

    private Boolean disabled;

    private RqStationInfo stationInfo;

    private String statusName;

    private String statusCode;

    private String workDayType;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date dateOfDismissal;

    public static RSManager get(Manager manager, RSRole role, String statusCode, String statusName) {
        return RSManager.builder()
                .id(manager.getId())
                .username(manager.getUsername())
                .firstName(manager.getFirstName())
                .lastName(manager.getLastName())
                .middleName(manager.getMiddleName())
                .fullName(manager.getLastName() + " " + manager.getFirstName().charAt(0) + ".")
                .email(manager.getEmail())
                .platform(Platform.valueOf(manager.getPlatform()).getValue())
                .role(role)
                .stationInfo(RqStationInfo.builder()
                        .agentId(manager.getAgentId())
                        .stationId(manager.getStationId())
                        .build())
                .statusCode(statusCode)
                .statusName(statusName)
                .workDayType(manager.getWorkDayStatus())
                .dateOfDismissal(manager.getDateOfDismissal())
                .build();
    }

    public static RSManager getOnlyFullName(Manager manager) {
        return RSManager.builder()
                .id(manager.getId())
                .fullName(manager.getLastName() + " " + manager.getFirstName().charAt(0) + ".")
                .build();
    }
}
