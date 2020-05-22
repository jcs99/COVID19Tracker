package pt.ipsantarem.esgts.covid19tracker.server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for various date related tasks.
 */
public class DateUtils {

    /**
     * Parses a String to a {@link java.time.LocalDate}
     *
     * @param date The String date
     * @return The parsed date
     * @throws ParseException If the parse was unsuccessful.
     */
    public static LocalDate parseStringToLocalDate(String date) throws ParseException {
        return dateToLocalDate(new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(date));
    }

    /**
     * Parses a String to a {@link java.util.Date}
     *
     * @param pattern The pattern of the passed String date
     * @param date    The String date
     * @return The parsed date
     * @throws ParseException If the parse was unsuccessful.
     */
    public static Date parseStringToDate(String pattern, String date) throws ParseException {
        return new SimpleDateFormat(pattern, Locale.ENGLISH).parse(date);
    }

    /**
     * Converts a {@link java.util.Date} to a {@link java.time.LocalDate}
     *
     * @param date The Date argument
     * @return The converted {@link java.time.LocalDate} date
     */
    public static LocalDate dateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Converts a {@link java.time.LocalDate} to a {@link java.util.Date}
     *
     * @param localDate The {@link java.time.LocalDate} argument
     * @return The converted {@link java.util.Date} date
     */
    public static Date localDateToDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    /**
     * Converts a {@link java.util.Date} to a {@link String}
     *
     * @param date The Date param
     * @return The Date converted to a String
     */
    public static String localDateToString(Date date) {
        LocalDate localDate = dateToLocalDate(date);
        return localDate.getDayOfMonth() + "/" + localDate.getMonthValue() + "/" + localDate.getYear();
    }
}
