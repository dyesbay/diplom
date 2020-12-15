package app.expert.parsers.file_parser;

import app.expert.parsers.file_parser.entity.RawRegionPhoneCode;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class FileRegionPhoneNumberParser {

    public static List<RawRegionPhoneCode> parse(String content)
            throws IOException { BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(content.getBytes()), StandardCharsets.UTF_8));
        List<RawRegionPhoneCode> result = new LinkedList<>();
        reader.readLine(); // опускаем первую строку
        // с помощью цикла парсим файл,
        // сливаем строки с одинаковыми кодами и регионами которые идут подряд
        // слияние ускоряет работу в 4 раза
        while(reader.ready()) {
            String[] currentStr = reader.readLine().split(";");
            if(currentStr.length < 3)
                break;
            RawRegionPhoneCode current = RawRegionPhoneCode.get(currentStr);
            result.add(current);
            //ищем следующую строку и если
            // регион и код следующей строки совпадает, сливаем с текущей и ищем следующую.
            while (reader.ready()) {
                String[] nextStr = reader.readLine().split(";");
                if(nextStr.length < 3)
                    break;
                RawRegionPhoneCode next = RawRegionPhoneCode.get(nextStr);
                if(current.equalsTo(next)) {
                    current = current.merge(next);
                } else {
                    result.add(next);
                    break;
                }
            }
        }
        reader.close();
        return result;
    }
}
