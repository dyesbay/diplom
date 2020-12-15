package app.expert.models.section;

import app.expert.db.statics.sections.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Component
public class RqSection {

    @NotNull
    private String name;
    private String description;

    public Section get(){
        return Section.builder()
                .name(name)
                .description(description)
                .build();
    }
}
