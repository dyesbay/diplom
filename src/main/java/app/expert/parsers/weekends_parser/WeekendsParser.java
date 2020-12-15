package app.expert.parsers.weekends_parser;

import app.base.utils.DateUtils;
import app.expert.db.schedule.general_schedule.GeneralScheduleCache;
import app.expert.services.ScheduleService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
@Data
public class WeekendsParser {

    private final ScheduleService scheduleService;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final GeneralScheduleCache generalScheduleCache;

    @Value("classpath:/weekends/weekends.csv")
    private Resource resource;


    private Date parseDay(String day, int month, String year) {
        if (day.contains("*")) return null;
        day = day.replace("+", "");
        int dayDate;
        dayDate =Integer.parseInt(day);
        String dayS;
        String monthS;
        if (dayDate < 10) {
            dayS = "0" + day;
        } else {
            dayS = day;
        }
        if (month < 10) {
            monthS = "0" + month;
        } else {
            monthS = String.valueOf(month);
        }
        String date = dayS + "." + monthS + "." + year;
        SimpleDateFormat format = new SimpleDateFormat(DateUtils.HUMAN_DATE);
        try {
            Date d = format.parse(date);
            return d;
        } catch (ParseException ignore) {

        }

        return null;
    }

    public void parse() {

        StringBuilder buffer = new StringBuilder();

        if (generalScheduleCache.getAll().size() != 0) {
            return;
        }

        try {
            InputStream inputStream = resource.getInputStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            char[] arr = new char[8 * 1024];
            int numCharsRead;
            while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
                buffer.append(arr, 0, numCharsRead);
            }
            reader.close();
        } catch (Exception ignore) {}
        String targetString = buffer.toString();
        String[] content = targetString.split("\n");

        // начнем сразу с первой строки потому на нулевой шапка таблицы
        int i = 1;
        List<Date> weekendsList = new ArrayList<>();
        while (i < content.length) {
            String[] yearData = content[i].split(";");
            String year = yearData[0];
            int month = 1;

            while (month < 13) {
                String[] weekends = yearData[month].split(",");
                for (String weekend : weekends) {
                    weekendsList.add(parseDay(weekend, month, year));
                }
                month++;
            }
            i++;
        }
        SimpleDateFormat format = new SimpleDateFormat(DateUtils.HUMAN_DATE);
      try {
          Date from = format.parse("01.01.2020");
          Date to = format.parse("01.01.2026");
          scheduleService.addWeekendsForAll(from, to, weekendsList);
          logger.info("-- added " + weekendsList.size() + " weekend days");
      } catch (ParseException ignore) {

      }
    }
}
