package root.utils;

import java.time.LocalTime;

public class LectureTimes {

    private static String[] lectureTimesStr =
            {
                    "8:30",
                    "10:25",
                    "12:35",
                    "14:30",
                    "16:25",
                    "18:10"
            };
    private static LocalTime[] lectureTimes =
            {
                    LocalTime.of(8,30),
                    LocalTime.of(10,25),
                    LocalTime.of(12,35),
                    LocalTime.of(14,30),
                    LocalTime.of(16,25),
                    LocalTime.of(18,10)
            };

    public static String[] valuesString(){
        return lectureTimesStr;
    }

    public static LocalTime[] valuesTime(){
        return lectureTimes;
    }

    public static LocalTime toTime(String time){
        return DTF.parseTime(time);
    }

    public static String toString(LocalTime time){
        return DTF.formatTime(time);
    }

}
