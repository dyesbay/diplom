package app.expert.db.statics.letters;

import app.base.db.GEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "letters")
public class Letter extends GEntity<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private String body;

    private String signature;

    private Long type;

    private Long sender;

    private Long request;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Moscow")
    @Column(name = "sent_on")
    private Date sent;
}
