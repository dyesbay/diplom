package app.expert.models.manager;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RSStatusChange {

    private String username;

    private String status;

    @JsonFormat(pattern = "HH:mm:ss dd.MM.yyyy", timezone = "Europe/Moscow")
    private Date start;

    @JsonFormat(pattern = "HH:mm:ss dd.MM.yyyy", timezone = "Europe/Moscow")
    private Date end;
}
