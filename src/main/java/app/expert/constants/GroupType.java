package app.expert.constants;

import app.base.objects.IGEnum;

public enum GroupType implements IGEnum {

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
