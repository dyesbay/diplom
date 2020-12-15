package app.expert.models.subject;

import app.expert.db.statics.subjects.Subject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Component
public class RqSubject {

    private String name;
    private String description;
    @NotNull
    private Long section;
    private Long maxExecutionTime;
    private boolean complaint;

    public Subject getSubject() {
        return Subject.builder()
                .name(name)
                .description(description)
                .section(section)
                .maxExecutionTime(maxExecutionTime)
                .complaint(complaint)
                .build();
    }
}
