package app.expert.constants;

import app.base.objects.IGEnum;

public enum Channel implements IGEnum {


    PHONE("Телефон"),
    EMAIL("Электронная почта"),
    CHAT("Чат"),
    ROSREESTR_PORTAL("Портал Росреестра"),
    ROSREESTRS_SITE("Сайт Росреестра"),
    INFORMATION_SERVICE("Сервис предоставления выписок"),
    OTHER("Иные")
    ;

    private final String channel;

    Channel(String channel) {
        this.channel = channel;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return channel;
    }
}
