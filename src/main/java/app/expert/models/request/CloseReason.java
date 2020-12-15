package app.expert.models.request;

import app.base.objects.IGEnum;
import java.util.Arrays;
import java.util.List;

public enum CloseReason implements IGEnum {

    RESPONSE_GIVEN("110", "207", "305", "405", "510"),
    VIOLATED_BY_CLIENT( "114", "213", "309", "409", "515"),
    VIOLATED_BY_TO_PO("391", "491"),
    VIOLATED_BY_ESROO("291"),
    IDENTICAL_REQUEST("0");

    private final List<String> statuses;

    CloseReason(String ... arg) {
        statuses = Arrays.asList(arg.clone());
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return null;
    }

    public List<String> getValues() {
        return statuses;
    }
}
