package app.expert.constants;

import app.base.objects.IGEnum;

public enum DayTypes implements IGEnum {

    WORK_DAY,
    SICK_LEAVE,
    WEEKEND,
    VACATION;

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }
}
