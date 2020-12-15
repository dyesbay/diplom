package app.expert.models.request;

import lombok.*;


import javax.validation.constraints.NotNull;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public class RqClose {

    @NotNull
    private Long id;
    @NotNull
    private CloseReason reason;
    private String comment;
}
