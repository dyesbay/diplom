package app.expert.models.region;

import app.expert.db.statics.region.Region;
import app.expert.db.statics.region_phone_numbers.RegionPhoneNumber;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class RsDEFRegionPhoneNumber {
    
    private Long id;
    private Short phoneCode;
    private String regionName;
    private Integer diapasonFrom;
    private Integer diapasonTo;

    public static RsDEFRegionPhoneNumber get(RegionPhoneNumber input, List<Region> regions) {
        return RsDEFRegionPhoneNumber.builder()
                .id(input.getId())
                .phoneCode(input.getCode())
                .regionName(findRegion(input, regions))
                .diapasonFrom(input.getDFrom())
                .diapasonTo(input.getDTo())
                .build();
    }

    public static String findRegion(RegionPhoneNumber input, List<Region> regions){
        return regions.stream()
                .filter(s -> input.getRegion().equals(s.getId()))
                .findFirst().get()
                .getName();
    }
}
