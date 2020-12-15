package app.expert.constants;

import app.base.objects.IGEnum;

public enum AdmissionEventTypes implements IGEnum {

    DEFERRED_RQ_REGISTRATION,
    START_CALL,
    COMPLETE_CALL,
    COMMENT_ADDED;

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }
}
