package app.expert.db.request;

import app.base.utils.SerializationUtils;
import app.expert.models.request.MField;

import javax.persistence.AttributeConverter;
import java.util.List;

public class IncomingRequestBodyConverter implements AttributeConverter<List<MField>, String> {

    @Override
    public String convertToDatabaseColumn(List<MField> info) {
        return SerializationUtils.toJson(info);
    }

    @Override
    public List<MField> convertToEntityAttribute(String dbData) {
        return SerializationUtils.fromJson(dbData, List.class);
    }
}
