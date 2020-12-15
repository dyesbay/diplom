package app.expert.models.region;

import app.expert.db.statics.region_phone_numbers.RegionPhoneNumber;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RqDEFRegionPhoneNumber {

    private Short code;
    private int from;
    private int to;
    private Long region;

    public RegionPhoneNumber get(){
        return RegionPhoneNumber.builder()
                .code(code)
                .region(region)
                .dTo(to)
                .dFrom(from)
                .build();
    }
}
