package app.expert.models.request;

import app.expert.constants.InputType;
import app.expert.db.statics.request_fields.RequestField;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Component
public class RqRequestField {

    @NotNull
    private String name;
    private InputType inputType;
    private Long subject;
    private Long subSubject;
    private boolean required;
    private String[] values;

    public RequestField get(){
        return RequestField.builder()
                .inputType(inputType)
                .name(name)
                .build();
    }
}
