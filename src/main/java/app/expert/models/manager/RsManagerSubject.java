package app.expert.models.manager;

import app.expert.models.subject.RsSubject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RsManagerSubject {

    private Long manager;

    private RsSubject subject;
}
