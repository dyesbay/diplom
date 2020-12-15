package app.expert.models;

import app.expert.db.event.Event;
import lombok.*;

import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RsEvent {

    private Long id;

    private Date created;

    private Long initiator;

    private String type;

    private Map<String, Object> body;

    private String commentary;

    private Long request;

    private String template;

    private Date disabled;

    private Date checked;

    public boolean isDisabled() {
        return disabled != null;
    }

    public static RsEvent getFromEntity(Event event, String template) {
        return RsEvent.builder()
                .id(event.getId())
                .body(event.getBody())
                .created(event.getCreated())
                .initiator(event.getInitiator())
                .type(event.getType())
                .commentary(event.getCommentary())
                .request(event.getRequest())
                .template(template)
                .disabled(event.getDisabled())
                .checked(event.getChecked())
                .build();
    }
}
