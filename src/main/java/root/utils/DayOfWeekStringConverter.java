package root.utils;

import javafx.util.StringConverter;

import java.time.DayOfWeek;

public class DayOfWeekStringConverter extends StringConverter<DayOfWeek> {
    @Override
    public String toString(DayOfWeek object) {
        if (object != null) {
            switch (object) {
                case MONDAY:
                    return "Понеділок";
                case TUESDAY:
                    return "Вівторок";
                case WEDNESDAY:
                    return "Середа";
                case THURSDAY:
                    return "Четвер";
                case FRIDAY:
                    return "П'ятниця";
                case SATURDAY:
                    return "Субота";
                case SUNDAY:
                    return "Неділя";
            }
        }
        return "Невірні дані!!";

    }

    @Override
    public DayOfWeek fromString(String string) {
        if (string != null) {
            switch (string) {
                case "Понеділок":
                    return DayOfWeek.MONDAY;
                case "Вівторок":
                    return DayOfWeek.TUESDAY;
                case "Середа":
                    return DayOfWeek.WEDNESDAY;
                case "Четвер":
                    return DayOfWeek.THURSDAY;
                case "П'ятниця":
                    return DayOfWeek.FRIDAY;
                case "Субота":
                    return DayOfWeek.SATURDAY;
                case "Неділя":
                    return DayOfWeek.SUNDAY;
            }
        }
        return DayOfWeek.MONDAY;
    }
}
