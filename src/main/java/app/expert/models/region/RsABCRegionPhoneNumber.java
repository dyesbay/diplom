package app.expert.models.region;

import app.expert.db.statics.region.Region;
import app.expert.db.statics.region_phone_numbers.RegionPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RsABCRegionPhoneNumber {

    private Long id;
    private String regionName;
    private Short code;

    public static RsABCRegionPhoneNumber get(RegionPhoneNumber item, List<Region> regions) {
        return RsABCRegionPhoneNumber.builder()
                .id(item.getId())
                .code(item.getCode())
                .regionName(findRegion(item.getRegion(), regions).getName())
                .build();
    }

    private static Region findRegion(Long region, List<Region> regions) {
        return regions.stream().filter(s -> s.getId().equals(region)).findFirst().get();
    }
}
