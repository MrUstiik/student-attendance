package root.utils;

import javafx.scene.control.Alert;
import root.models.Lecture;
import root.models.ScheduleLecture;
import root.models.enums.LectureRotation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LectureGenerator {
    public static List<Lecture> generateLectures(List<ScheduleLecture> scheduleLectures, LocalDate from, LocalDate to,
                                                 int week) throws IllegalArgumentException {
        if (week != 1 && week != 2) {
            throw new IllegalArgumentException("Incorrect week value!!");
        }
        List<Lecture> generatedLectures = new LinkedList<>();
        for (ScheduleLecture sl : scheduleLectures) {
            int step = sl.getRotation() == LectureRotation.EVERY_WEEK ? 7 : 14;
            try {
                for (LocalDate i = firstLectureDay(sl, from, to, week); i.isBefore(to); i = i.plusDays(step)) {
                    generatedLectures.add(new Lecture(
                            sl.getType(),
                            LocalDateTime.of(i, DTF.parseTime(sl.getTime())),
                            sl.getAudience(),
                            ""));
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка!!");
                alert.setHeaderText(e.getMessage());
                alert.setWidth(300);
                alert.showAndWait();
                return new LinkedList<>();
            }
        }
        Collections.sort(generatedLectures);
        return generatedLectures;
    }

    private static LocalDate firstLectureDay(ScheduleLecture sl, LocalDate from, LocalDate to, int week) throws Exception {
        boolean firstWeek = week == 1;
        for (LocalDate i = from; i.isBefore(to); i = i.plusDays(1)) {
            if (i.getDayOfWeek() == DayOfWeek.MONDAY && !i.equals(from)) {
                firstWeek = !firstWeek;
            }
            if (sl.getRotation() == LectureRotation.EVERY_WEEK ||
                    (firstWeek && (sl.getRotation() == LectureRotation.FIRST_WEEK))
                    || (!firstWeek && (sl.getRotation() == LectureRotation.SECOND_WEEK))) {
                if (i.getDayOfWeek().equals(sl.getDay())) {
                    return i;
                }
            }
        }

        throw new Exception("Немає прийнятної дати для заняття " + sl + ": від " + from + " до " + to + "!!");
    }

}
