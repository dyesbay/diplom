package app.expert.constants;

import app.base.objects.IGEnum;

public enum EventTypeValues implements IGEnum {

    REQUEST_CREATED,
    STATUS_CHANGED,
    ROUTE_CHANGED,
    EMAIL_SENT,
    COMMENT_ADDED,
    RESPONSE_ADDED,
    REQUEST_CLOSED;

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }
}
