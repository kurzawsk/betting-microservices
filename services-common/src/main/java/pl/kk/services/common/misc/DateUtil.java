package pl.kk.services.common.misc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static LocalDateTime getDate(String day, String month, String year, String hour, String minute) {

        int iYear = Integer.parseInt(year);
        int iMonth = Integer.parseInt(month);
        int iDay = Integer.parseInt(day);
        int iHour = Integer.parseInt(hour);
        int iMinute = Integer.parseInt(minute);

        if (iDay < 1 || iDay > 31) {
            throw new IllegalArgumentException("Provided day of month is invalid: " + day);
        }

        if (iMonth < 1 || iMonth > 12) {
            throw new IllegalArgumentException("Provided  month is invalid: " + month);
        }

        if (iHour < 0 || iHour > 24) {
            throw new IllegalArgumentException("Provided hour is invalid: " + hour);
        }

        if (iMinute < 0 || iMinute > 60) {
            throw new IllegalArgumentException("Provided day of minute is invalid: " + minute);
        }

        return LocalDateTime.of(iYear, iMonth, iDay, iHour, iMinute);

    }

    public static String getCurrentDateInFormat(String format) {
        return getDateInFormat(LocalDate.now(), format);
    }

    public static String getDateInFormat(LocalDate date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }


}
