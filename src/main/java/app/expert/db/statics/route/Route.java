package app.expert.db.statics.route;

import app.base.db.GEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "routes")
public class Route extends GEntity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String method;

    private String path;

    private Boolean open;

    private Boolean system;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @Column(name = "disabled_on")
    private Date disabled;
    
    @Override
    public boolean isDisabled() {
        return disabled != null;
    }
}
