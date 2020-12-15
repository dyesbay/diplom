package app.expert.models.request;

import app.expert.constants.Platform;
import app.expert.db.request.Request;
import app.expert.models.RsEvent;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static app.base.utils.DateUtils.HUMAN_DATE;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class RsRequest {

    private Long id;

    private Long subject;

    private Long subSubject;

    private List<MField> data;

    private Map<String, Object> clientContacts;

    private Long clientId;

    private String channel;

    private String status;

    private String text;

    private Long assigned;

    private Long admission;

    private Platform platform;

    private Long link;

    private List<RsEvent> events;

    @JsonFormat(pattern = HUMAN_DATE, timezone = "Europe/Moscow")
    private Date expiresDate;

    @JsonFormat(pattern = HUMAN_DATE, timezone = "Europe/Moscow")
    private Date lifeSpanDate;

    public static RsRequest get(Request requestEntity) {
        return RsRequest.builder()
                .id(requestEntity.getId())
                .channel(requestEntity.getChannel().getKey())
                .clientContacts(requestEntity.getApplicantContacts())
                .clientId(requestEntity.getRequester())
                .data(requestEntity.getBody())
                .status(requestEntity.getStatus())
                .subject(requestEntity.getSubject())
                .subSubject(requestEntity.getSubSubject())
                .text(requestEntity.getText())
                .id(requestEntity.getId())
                .assigned(requestEntity.getAssignee())
                .admission(requestEntity.getAdmission())
                .platform(requestEntity.getPlatform())
                .link(requestEntity.getLink())
                .expiresDate(requestEntity.getExpiresDate())
                .lifeSpanDate(requestEntity.getLifeSpanDate())
                .build();
    }
}
