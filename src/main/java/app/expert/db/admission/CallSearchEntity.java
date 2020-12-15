package app.expert.db.admission;

import app.base.db.GEntity;
import app.expert.db.manager.Manager;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "calls")
public class CallSearchEntity extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manager", referencedColumnName = "id", insertable = false, updatable = false)
    private Manager manager;
}
