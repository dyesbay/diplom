package app.expert.constants;

import app.base.objects.IGEnum;

public enum Configs implements IGEnum {

     ACTIVE_DIRECTORY_SERVER_KURSK_1("10.100.10.10"),
    ACTIVE_DIRECTORY_SERVER_KURSK_2("10.100.10.11"),
    ACTIVE_DIRECTORY_SERVER_KAZAN("10.101.18.50"),
    ACTIVE_DIRECTORY_KURSK_DOMAIN("vkr.kadastr.ru"),
    ACTIVE_DIRECTORY_KAZAN_DOMAIN("vkz.kadastr.ru"),

    SUPERUSER_SALT("c03d2499-9940-4cd7-a1cb-6d506eb8db15");

     private final String value;

     Configs(String value) {
         this.value = value;
     }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return this.value;
    }

}
