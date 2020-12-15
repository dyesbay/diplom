package app.expert.constants;

import app.base.objects.IGEnum;

public enum LogType implements IGEnum {


    REQUEST("request"),
    RESPONSE("response"),
    EVENT_OZ("event_oz"),
    EVENT_ADMISSION("event_admission"),
    OTHER("other"),
    ;

    private final String value;

    LogType(String value) {
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
