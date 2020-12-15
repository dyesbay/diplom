package app.expert.constants;

import app.base.objects.IGEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Platform implements IGEnum {
    KAZAN("Казань","vkz"),
    KURSK("Курск","vkr")
    ;

    private final String name;
    private final String extCode;

    public static boolean contains(String city) {
        for (Platform platform : Platform.values()) {
            if (platform.name.equalsIgnoreCase(city)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return name;
    }
}
