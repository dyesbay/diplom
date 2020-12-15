package app.expert.models.region;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class FileInfo {

    private String name;
    private String size;
    private String date;
}
