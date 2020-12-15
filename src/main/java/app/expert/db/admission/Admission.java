package app.expert.db.admission;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "admissions")
public class Admission extends GEntity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientEmail;

    private String clientPhone;

    private String clientName;

    private Long client;

    private Long subject;

    private Long call;

    private String result;

    private String comment;

    @Column(name = "created_on")
    private Date created;

    @Column(name = "disabled_on")
    private Date disabled;
    
    @Override
    public boolean isDisabled() {
        return disabled != null;
    }
}
