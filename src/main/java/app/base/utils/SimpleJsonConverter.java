package app.base.utils;

import javax.persistence.AttributeConverter;
import java.util.Map;

public class SimpleJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Object> info) {
        return SerializationUtils.toJson(info);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        return SerializationUtils.fromJson(dbData, Map.class);
    }
}
