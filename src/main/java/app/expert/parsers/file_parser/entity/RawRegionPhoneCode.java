package app.expert.parsers.file_parser.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class RawRegionPhoneCode {

    private static final String[] remove_filter = {" /Якутия/"};
    private static final Map<String, String> replace_filter = new LinkedHashMap(){{
        put("г. Санкт - Петербург", "г. Санкт-Петербург");
        put("Московская область * Москва", "Московская обл.");
        put("Свердловская область", "Свердловская обл.");
        put("Ханты - Мансийский - Югра АО", "Ханты-Мансийский АО - Югра");
        put("Республика Удмуртская", "Удмуртская Республика");
        put("Республика Чеченская", "Чеченская Республика");
        put("Республика Карачаево-Черкесская","Карачаево-Черкесская Республика");
        put("Архангельская область и Ненецкий автономный округ",
                "Архангельская обл. и Ненецкий АО");
        put("Архангельская область * Ненецкий автономный округ",
                "Архангельская обл. и Ненецкий АО");
        put("Республика Кабардино-Балкарская","Кабардино-Балкарская Республика");
        put("область", "обл.");
    }};

    protected String code;
    protected int from;
    protected int to;
    protected int capacity;
    protected String operator;
    protected String region;

    public static RawRegionPhoneCode get(String[] lineFromFile){
        return RawRegionPhoneCode.builder()
                .code(lineFromFile[0])
                .from(Integer.parseInt(lineFromFile[1]))
                .to(Integer.parseInt(lineFromFile[2]))
                .region(extractRegion(lineFromFile[lineFromFile.length-1]))
                .build();
    }

    public boolean equalsTo(RawRegionPhoneCode item) {
        return this.code.equals(item.getCode()) && this.region.equals(item.getRegion());
    }

    public RawRegionPhoneCode merge(RawRegionPhoneCode item){
        return RawRegionPhoneCode.builder()
                .code(this.code)
                .from(this.from)
                .to(item.to)
                .capacity(0)
                .operator(null)
                .region(item.getRegion())
                .build();
    }

    private static String extractRegion(String input) {
        return filter(input.substring(input.lastIndexOf("|") + 1));
    }

    private static String filter(String input) {
        for (String str : remove_filter)
            input = input.replaceAll(str, "");
        for(Map.Entry<String, String> pair : replace_filter.entrySet()) {
            input = input.replaceAll(pair.getKey(), pair.getValue());
        }
        return input;
    }
}
