package app.expert.db.statics.names;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "names")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Name extends GEntity<Long> {

    @Id
    private Long id;
    private String name;
    @Column(name = "name_count")
    private Long nameCount;
}
