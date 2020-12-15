package app.expert.db.statics.request_comments;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests_comments")
public class RequestComment extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    private Long author;

    private Long request;
}
