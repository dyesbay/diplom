package app.expert.constants;

import app.base.objects.IGEnum;

public enum ExternalServicesCodes implements IGEnum {

    ESROO,
    CONFLUENCE,
    EGRN,
    UNSI,
    TELEPHONY,
    ACTIVE_DIRECTORY,
    EMAIL_SERVER;

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }
}
