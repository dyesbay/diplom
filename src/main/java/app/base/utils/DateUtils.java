package app.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    private DateUtils() {
    }

    public static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String HUMAN_DATE = "dd.MM.yyyy";
    public static final String HUMAN_TIME = "HH:mm:ss";
    public static final String HUMAN_DATE_TIME = HUMAN_DATE + " " + HUMAN_TIME;

    public static final String SYSTEM_DATE = "yyyy-MM-dd";
    public static final String SYSTEM_DATE_TIME = SYSTEM_DATE + " " + HUMAN_TIME;

    public static String format(Date date, String format, TimeZone timeZone) {
        if (date == null || ObjectUtils.isBlank(format)) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(timeZone);
        return sdf.format(date);
    }


    public static String format(Date date, String format) {
        return format(date, format, TimeZone.getDefault());
    }

    public static String formatHumanDate(Date date) {
        return format(date, HUMAN_DATE);
    }

    public static String formatHumanTime(Date date) {
        return format(date, HUMAN_TIME);
    }

    public static String formatHumanDateTime(Date date) {
        return format(date, HUMAN_DATE_TIME);
    }

    public static String formatSystemDate(Date date) {
        return format(date, SYSTEM_DATE);
    }

    public static String formatSystemDateTime(Date date) {
        return format(date, SYSTEM_DATE_TIME);
    }


    public static Date parse(String date, String format) {
        if (ObjectUtils.isBlank(date) || ObjectUtils.isBlank(format)) return null;
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    public static Date parseHumanDate(String date) {
        return parse(date, HUMAN_DATE);
    }

    public static Date parseHumanDateTime(String date) {
        return parse(date, HUMAN_DATE_TIME);
    }

    public static Date parseSystemDate(String date) {
        return parse(date, SYSTEM_DATE);
    }

    public static Date parseSystemDateTime(String date) {
        return parse(date, SYSTEM_DATE_TIME);
    }

    public static Date parseCardExpireDate(String expDate, String format) {
        YearMonth ym = YearMonth.parse(expDate, DateTimeFormatter.ofPattern(format));
        ZoneId defaultZoneId = ZoneId.systemDefault();
        return Date.from(ym.atEndOfMonth().atStartOfDay(defaultZoneId).toInstant());
    }


    public static Date toUtc(Date date) {
        return parse(format(date, DateUtils.SYSTEM_DATE_TIME, TimeZone.getTimeZone("UTC")), SYSTEM_DATE_TIME);
    }

    public static Date convert(Date date, String toFormat) {
        return parse(format(date, toFormat), toFormat);
    }

    public static int compare(Date d1, Date d2, String format) {
        d1 = convert(d1, format);
        d2 = convert(d2, format);

        if (d1 == null && d2 == null) return 0;
        if (d1 == null) return -1;
        if (d2 == null) return 1;

        return d1.compareTo(d2);
    }

    public static Long diff(Date first, Date last) {
        return last.getTime() - first.getTime();
    }

    public static Long diffNow(Date first) {
        return diff(first, new Date());
    }

    public static Date getFutureDate(Long secs) {
        return new Date((new Date()).getTime() + (1000 * secs));
    }

}
