package app.expert.models.call;

import app.base.utils.DateUtils;
import app.expert.db.call.Call;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Component;
import java.util.Date;

@RequiredArgsConstructor
@Setter
@Getter
@Component
public class RqCompCall {

    @NotBlank
    private String identifier;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private Date ended;

    @JsonFormat(pattern = DateUtils.HUMAN_TIME)
    private Date onHold;
    
    public Call getCall() {
        return Call.builder()
            .ended(ended)
            .onHold(onHold)
            .identifier(identifier)
            .build();
    }
}
