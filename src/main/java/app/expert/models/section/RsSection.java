package app.expert.models.section;

import app.base.exceptions.GException;
import app.expert.db.statics.sections.Section;
import app.expert.models.subject.RsSubject;
import app.expert.services.SubjectService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Component
public class RsSection {

    private Long id;
    private String header;
    private List<RsSubject> subjects;

    public static RsSection get(Section section, SubjectService subService) throws GException {
        return RsSection.builder()
                .id(section.getId())
                .header(section.getName())
                .subjects(subService.getBySection(section.getId()))
                .build();
    }
}
