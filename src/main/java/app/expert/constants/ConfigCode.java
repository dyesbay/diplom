package app.expert.constants;

import app.base.objects.IGEnum;

public enum ConfigCode implements IGEnum {

    REQUEST_DISTRIBUTION,
    REQUEST_DISTRIBUTION_TIME;


    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }


}
