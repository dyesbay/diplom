package app.expert.db.statics.manager_subject;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "manager_subject")
@IdClass(ManagerSubjectKey.class)
public class ManagerSubject extends GEntity<ManagerSubjectKey> {

    @Id
    private Long manager;

    @Id
    private Long subject;
}
