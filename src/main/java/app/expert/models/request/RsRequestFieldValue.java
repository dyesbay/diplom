package app.expert.models.request;

import app.expert.db.statics.request_field_values.RequestFieldValue;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RsRequestFieldValue {

    private Long id;
    private Long field;
    private String value;

    public static RsRequestFieldValue get(RequestFieldValue rq) {
        return RsRequestFieldValue.builder()
                .id(rq.getId())
                .field(rq.getField())
                .value(rq.getValue())
                .build();
    }
}
