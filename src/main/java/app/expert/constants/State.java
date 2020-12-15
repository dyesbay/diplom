package app.expert.constants;

import app.base.objects.IGEnum;

public enum State implements IGEnum {


    PENDING("PENDING"),

    IN_PROCESSING("IN_PROCESSING"),

    CLOSED("CLOSED"),
    EXPIRED("EXPIRED"),
    EXTERNAL_SERVICE("EXTERNAL_SERVICE");

    private final String status;

    State(String status) {
        this.status = status;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return status;
    }
}
