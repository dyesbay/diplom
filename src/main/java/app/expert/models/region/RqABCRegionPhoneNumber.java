package app.expert.models.region;

import app.expert.db.statics.region_phone_numbers.RegionPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class RqABCRegionPhoneNumber {

    @NotNull
    private Short code;
    @NotNull
    private Long region;

    public RegionPhoneNumber get(){
        return RegionPhoneNumber.builder()
                .dTo(0)
                .dFrom(0)
                .region(region)
                .code(code)
                .build();
    }
}
