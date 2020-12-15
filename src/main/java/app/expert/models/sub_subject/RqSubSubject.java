package app.expert.models.sub_subject;

import app.expert.db.statics.sub_subject.SubSubject;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class RqSubSubject {

    private String name;
    private String description;
    @NotNull
    private Long subject;
    private Long maxExecutionTime;
    private boolean complaint;

    public SubSubject getSubSubject() {
        return SubSubject.builder()
                .name(name)
                .description(description)
                .subject(subject)
                .maxExecutionTime(maxExecutionTime)
                .build();
    }
}
