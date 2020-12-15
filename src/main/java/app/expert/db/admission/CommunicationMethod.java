package app.expert.db.admission;

import app.base.objects.IGEnum;

public enum CommunicationMethod implements IGEnum {

    PHONE("Телефон"),
    EMAIL("Электронная почта");

    private String value;

    CommunicationMethod(String value){
        this.value = value;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }
}
