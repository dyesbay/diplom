package app.expert.db.statics.surnames;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "surnames")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Surname extends GEntity<Long> {

    @Id
    private Long id;
    private String name;
    @Column(name = "name_count")
    private Long nameCount;
}
