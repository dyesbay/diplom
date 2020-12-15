package app.expert.db.admission;

import app.base.db.GEntity;
import app.expert.db.client.Client;
import app.expert.db.statics.subjects.Subject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "admissions")
public class AdmissionSearchEntity extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client", referencedColumnName = "id", insertable = false, updatable = false)
    private Client client;

    @Column(name = "created_on")
    private Date created;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject", referencedColumnName = "id", insertable = false, updatable = false)
    private Subject subject;

    @OneToOne
    @JoinColumn(name = "call", referencedColumnName = "id", insertable = false, updatable = false)
    private CallSearchEntity callSearchEntity;

    @JsonIgnore
    @Column(name = "disabled_on")
    private Date disabled;
}
