package app.expert.models.request;

import app.expert.db.statics.request_comments.RequestComment;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RsRequestComment {

    private Long id;

    private String text;

    private Long author;

    private Long request;

    public static RsRequestComment getFromEntity(RequestComment comment) {
        return RsRequestComment.builder()
                .id(comment.getId())
                .author(comment.getAuthor())
                .text(comment.getComment())
                .request(comment.getRequest())
                .build();
    }
}
