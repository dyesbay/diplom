package app.expert.constants;

import app.base.objects.IGEnum;

public enum AccessType implements IGEnum {

    ALL,
    EXCLUDING,
    INCLUDING;

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }
}
