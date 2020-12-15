package app.expert.models.admission;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RqAdmission {

    private String clientName;

    private String email;

    @NotNull
    private String phone;

    private String result;

    private Long subject;
}
