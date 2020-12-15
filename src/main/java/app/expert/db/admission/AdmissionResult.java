package app.expert.db.admission;

import app.base.objects.IGEnum;

public enum AdmissionResult implements IGEnum {

    SUCCESS("Успешно обработан"),
    INTERRUPTED("Обарвалась связь"),
    COMPLETED_BY_OPERATOR("Завершено по инициативе оператора");

    private String value;

    AdmissionResult(String value){
        this.value = value;
    }

    @Override
    public String getKey() {
        return getKey();
    }

    @Override
    public String getValue(){
        return getValue();
    }

}
