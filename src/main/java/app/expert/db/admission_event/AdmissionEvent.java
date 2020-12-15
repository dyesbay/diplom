package app.expert.db.admission_event;

import app.base.db.GEntity;
import app.base.utils.SimpleJsonConverter;
import app.expert.constants.AdmissionEventTypes;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "admission_events")
public class AdmissionEvent extends GEntity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long initiator;

    @Enumerated(EnumType.STRING)
    private AdmissionEventTypes type;

    @Convert(converter = SimpleJsonConverter.class)
    private Map<String, Object> body;

    private Long admission;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Moscow")
    @Column(name = "created_on")
    private Date created;
}
