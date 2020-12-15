package app.expert.db.event;

import app.base.db.GEntity;
import app.base.utils.SimpleJsonConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "events")
public class Event extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss",  timezone = "Europe/Moscow")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    private Date created;

    private Long initiator;

    private String type;

    @Convert(converter = SimpleJsonConverter.class)
    private Map<String, Object> body;

    private String commentary;

    private Long request;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @Column(name = "disabled_on")
    private Date disabled;

    private Date checked;

    @Override
    public boolean isDisabled() {
        return disabled != null;
    }
}
