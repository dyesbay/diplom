package app.expert.db.statics.route_type;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "route_types")
public class RouteType extends GEntity<String> {

    @Id
    private String code;
    private String name;
    private String description;
}
