package root.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * DateTimeFormatter
 */

public class DTF {
    private static final String timePattern = "H:mm";
    private static final String datePattern = "dd.MM.yy";
    private static final String isoPattern = "yyyy-MM-dd'T'HH:mm:ss ";

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timePattern);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
    private static final DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern(isoPattern);

    public static String getTimePattern() {
        return timePattern;
    }

    public static String getDatePattetn() {
        return datePattern;
    }

    public static DateTimeFormatter getTimeFormatter() {
        return timeFormatter;
    }

    public static DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public static LocalTime parseTime(String time) {
        return LocalTime.parse(time, timeFormatter);
    }

    public static String formatTime(LocalTime time) {
        return time.format(timeFormatter);
    }

    public static String formatTime(LocalDateTime time) {
        return time.format(timeFormatter);
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, dateFormatter);
    }

    public static String formatDate(LocalDate date) {
        return date.format(dateFormatter);
    }

    public static String formatDate(LocalDateTime date) {
        return date.format(dateFormatter);
    }

    public static String formatDateTimeToIso(LocalDateTime dateTime){
        return dateTime.format(isoFormatter);
    }

    public static LocalDateTime parseDateTimeIso(String dateTime){
        return LocalDateTime.parse(dateTime, isoFormatter);
    }

//    public static LocalDateTime parseDateTime(String dateTime){
////        dateTime = dateTime.replace("\n", " ");
//        return LocalDateTime.of(LocalDate.parse(dateTime, dateFormatter), LocalTime.parse(dateTime, timeFormatter));
//    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime.toLocalDate(), dateTime.toLocalTime());
    }

    public static String formatDateTime(LocalDate date, LocalTime time) {
        return concat(date.format(dateFormatter), time.format(timeFormatter), " ");
    }

    public static String formatDateTimeln(LocalDateTime dateTime) {
        return formatDateTimeln(dateTime.toLocalDate(), dateTime.toLocalTime());
    }

    public static String formatDateTimeln(LocalDate date, LocalTime time) {
        return concat(date.format(dateFormatter), time.format(timeFormatter), "\n");
    }

    private static String concat(String date, String time, String separator) {
        return date + separator + time;
    }


//    public static String toDateTime(String date, String time) {
//        return concat(date, time, " ");
//    }
//
//    public static String toDateTime(LocalDate date, LocalTime time){
//        return concat(date, time, " ");
//    }
//
//    public static String toDateTimeln(String date, String time){
//        return concat(date, time, "\n");
//    }
//
//    public static String toDateTimeln(LocalDate date, LocalTime time){
//        return concat(date, time, "\n");
//    }
//
//    private static String concat(LocalDate date, LocalTime time, String separator){
//        return new StringBuilder().
//                append(date.format(dateFormatter)).
//                append(separator).
//                append(time.format(timeFormatter)).
//                toString();
//    }
//

//
//    private static String concat1(String date, String time, String separator){
//        return concat(LocalDate.parse(date, dateFormatter),
//                LocalTime.parse(time, timeFormatter), separator);
//    }

}
