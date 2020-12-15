package app.expert.models;

import app.base.objects.GObject;
import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.db.admission.AdmissionSearchEntity;
import app.expert.db.admission.CommunicationMethod;
import app.expert.db.request.RequestSearchEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

import static app.base.utils.DateUtils.HUMAN_DATE;
import static app.base.utils.ObjectUtils.isNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsRequestOrAdmission extends GObject {

    private Long id;
    private String subject;
    private String channel;
    private String routeType;
    private String communicationMethod;
    private String status;
    private String statusCode;
    private String type;
    private String platform;
    private String client;
    private String assignee;
    private String text;
    private String color;

    @JsonFormat(pattern = HUMAN_DATE, timezone = "Europe/Moscow")
    private Date expiresDate;

    @JsonFormat(pattern = HUMAN_DATE, timezone = "Europe/Moscow")
    private Date lifeSpanDate;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Moscow")
    private Date created;

    public static RsRequestOrAdmission get(RequestSearchEntity request) {
        return RsRequestOrAdmission.builder()
                .id(request.getId())
                .subject(request.getSubject().getName())
                .channel(request.getChannel().getValue())
                .routeType(!isNull(request.getRouteType()) ? request.getRouteType().getName() : null)
                .communicationMethod(!isNull(request.getRequester()) ? request.getRequester().getCommunicationType().getValue() : null)
                .status(!isNull(request.getStatus()) ? request.getStatus().getName() : null)
                .statusCode(!isNull(request.getStatus()) ? request.getStatus().getCode() : null)
                .type("request")
                .platform(request.getPlatform().getValue())
                .client(!isNull(request.getRequester()) ? request.getRequester().getFullName() : null)
                .assignee(!isNull(request.getAssignee()) ? request.getAssignee().getFullName() : "")
                .created(request.getCreated())
                .text(request.getText())
                .color(!isNull(request.getStatus()) ? request.getStatus().getColor() : null)
                .expiresDate(request.getExpiresDate())
                .lifeSpanDate(request.getLifeSpanDate())
                .build();

    }

    public static RsRequestOrAdmission get(AdmissionSearchEntity admission) {
        return RsRequestOrAdmission.builder()
                .id(admission.getId())
                .subject(!isNull(admission.getSubject()) ? admission.getSubject().getName() : null)
                .communicationMethod(
                        admission.getClient() == null || admission.getClient().getCommunicationType() == null
                                ?
                                CommunicationMethod.PHONE.getValue()
                                :
                                admission.getClient() .getCommunicationType().getValue())
                .channel(Channel.PHONE.getValue())
                .created(admission.getCreated())
                .type("admission")
                .client(admission.getClient()  == null ? "" : admission.getClient().getFullName())
                .platform(Platform.valueOf(admission.getCallSearchEntity().getManager().getPlatform()).getValue())
                .assignee(admission.getCallSearchEntity().getManager().getFullName())
                .build();
    }
}
