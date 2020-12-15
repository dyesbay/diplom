package app.expert.db.statics.region_phone_numbers;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "region_phone_numbers")
public class RegionPhoneNumber extends GEntity<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Short code;
    private Integer dFrom;
    private Integer dTo;
    private Long region;

    public boolean equalsTo(RegionPhoneNumber regionPhoneNumber){
        return this.code.equals(regionPhoneNumber.getCode()) &&
                this.dFrom.equals(regionPhoneNumber.getDFrom()) &&
                this.dTo.equals(regionPhoneNumber.getDTo()) &&
                this.region.equals(regionPhoneNumber.getRegion());

    }
}
