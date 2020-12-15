package app.expert.db.statics.manager_subject;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@Embeddable
public class ManagerSubjectKey implements Serializable {

    @Id
    private Long manager;

    @Id
    private Long subject;
}
