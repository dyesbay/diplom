package app.expert.constants;

import app.base.objects.IGEnum;

public enum InputType implements IGEnum {
    
    TEXT,
    SELECT,
    TEXT_SEARCH,
    EXTERNAL_SERVICE,
    DATE,
    DATE_TIME,
    BOOLEAN;

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }
}
