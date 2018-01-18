package root.models;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import root.models.enums.Attendance;
import root.models.enums.LectureType;
import root.utils.DTF;

import java.time.LocalDateTime;
import java.util.Map;

public class Lecture implements Comparable<Lecture> {
    private final ObjectProperty<LectureType>   type;
    private final ObjectProperty<LocalDateTime> dateTime;
    private final StringProperty                audience;
    private final StringProperty                note;

//    private final ObservableMap<Student, Attendance> attendance = FXCollections.observableHashMap();

    public Lecture() {
        type = new SimpleObjectProperty<>(LectureType.LECTURE);
        dateTime = new SimpleObjectProperty<>(LocalDateTime.now());
        audience = new SimpleStringProperty("");
        note = new SimpleStringProperty("");
    }

    public Lecture(ObjectProperty<LectureType> type, ObjectProperty<LocalDateTime> dateTime, StringProperty audience,
                   StringProperty note) {
        this.type = type;
        this.dateTime = dateTime;
        this.audience = audience;
        this.note = note;
    }

    public Lecture(LectureType type, LocalDateTime dateTime, String audience, String note) {
        this.type = new SimpleObjectProperty<>(type);
        this.dateTime = new SimpleObjectProperty<>(dateTime);
        this.audience = new SimpleStringProperty(audience);
        this.note = new SimpleStringProperty(note);
    }

//    public Lecture(ObjectProperty<LectureType> type, ObjectProperty<LocalDateTime> dateTime, StringProperty audience,
//                   StringProperty note, ObservableMap<Student, Attendance> attendance) {
//        this.type = type;
//        this.dateTime = dateTime;
//        this.audience = audience;
//        this.note = note;
//        this.attendance = attendance;
//    }
//
//    public Lecture(LectureType type, LocalDateTime dateTime, String audience, String note, Map<Student, Attendance> attendance) {
//        this.type = new SimpleObjectProperty<>(type);
//        this.dateTime = new SimpleObjectProperty<>(dateTime);
//        this.audience = new SimpleStringProperty(audience);
//        this.note = new SimpleStringProperty(note);
//        this.attendance = FXCollections.observableMap(attendance);
//    }

    public LectureType getType() {
        return type.get();
    }

    public ObjectProperty<LectureType> typeProperty() {
        return type;
    }

    public void setType(LectureType type) {
        this.type.set(type);
    }

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime.set(dateTime);
    }

    public String getAudience() {
        return audience.get();
    }

    public StringProperty audienceProperty() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience.set(audience);
    }

    public String getNote() {
        return note.get();
    }

    public StringProperty noteProperty() {
        return note;
    }

    public void setNote(String note) {
        this.note.set(note);
    }

//    public ObservableMap<Student, Attendance> getAttendance() {
//        return attendance;
//    }
//
//    public void setAttendance(ObservableMap<Student, Attendance> attendance) {
//        this.attendance = attendance;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lecture)) return false;

        Lecture lecture = (Lecture) o;

        return dateTime != null ? dateTime.get().equals(lecture.dateTime.get()) : lecture.dateTime == null;
    }

    @Override
    public int hashCode() {
        return dateTime != null ? dateTime.get().hashCode() : 0;
    }

    @Override
    public String toString() {
        return DTF.formatDateTimeln(dateTime.get()) + "\n" + audience.get();
    }

    @Override
    public int compareTo(Lecture o) {
        return getDateTime().compareTo(o.getDateTime());
    }
}
