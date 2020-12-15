package app.expert.db.configs;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "configs")
public class Config extends GEntity<String> {

    @Id
    private String key;

    private String value;
}
