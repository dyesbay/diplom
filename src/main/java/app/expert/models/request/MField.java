package app.expert.models.request;

import app.base.objects.GObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * requestField - айди поля из таблицы request_fields
 * value - поле не пустое если у requestField тип поля TEXT (ручной ввод)
 * requestFieldValue - не пустое если у requestField тип поля TEXT_SEARCH (выпадающий список)
 */

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class MField extends GObject {

    private Long requestField;

    private String requestFieldName;

    private String value;

    private Long requestFieldValue;
}
