package app.expert.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Component
@Setter
@Getter
public class RqEventType {
    
    @NotNull private String code;
    @NotNull private String name;
    @NotNull private String description;
    @NotNull private String template;
}
