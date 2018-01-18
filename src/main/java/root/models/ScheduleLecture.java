package root.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import root.models.enums.LectureRotation;
import root.models.enums.LectureType;
import root.utils.DTF;
import root.utils.LectureTimes;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class ScheduleLecture {
    private final ObjectProperty<LectureType>     type;
    private final ObjectProperty<DayOfWeek>       day;
    private final ObjectProperty<LectureRotation> rotation;
    private final StringProperty                  time;
    private final StringProperty                  audience;

    public ScheduleLecture() {
        type = new SimpleObjectProperty<>(LectureType.LECTURE);
        day = new SimpleObjectProperty<>(DayOfWeek.MONDAY);
        rotation = new SimpleObjectProperty<>(LectureRotation.EVERY_WEEK);
        time = new SimpleStringProperty(LectureTimes.valuesString()[0]);
        audience = new SimpleStringProperty("");
    }

    public ScheduleLecture(ObjectProperty<LectureType> type,
                           ObjectProperty<DayOfWeek> day, ObjectProperty<LectureRotation> rotation,
                           StringProperty time, StringProperty audience) {
        this.type = type;
        this.day = day;
        this.rotation = rotation;
        this.time = time;
        this.audience = audience;
    }

    public ScheduleLecture(LectureType type, DayOfWeek day, LectureRotation rotation, String time, String audience) {
        this.type = new SimpleObjectProperty<>(type);
        this.day = new SimpleObjectProperty<>(day);
        this.rotation = new SimpleObjectProperty<>(rotation);
        this.time = new SimpleStringProperty(time);
        this.audience = new SimpleStringProperty(audience);
    }

    public LectureType getType() {
        return type.get();
    }

    public ObjectProperty<LectureType> typeProperty() {
        return type;
    }

    public void setType(LectureType type) {
        this.type.set(type);
    }

    public DayOfWeek getDay() {
        return day.get();
    }

    public ObjectProperty<DayOfWeek> dayProperty() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day.set(day);
    }

    public LectureRotation getRotation() {
        return rotation.get();
    }

    public ObjectProperty<LectureRotation> rotationProperty() {
        return rotation;
    }

    public void setRotation(LectureRotation rotation) {
        this.rotation.set(rotation);
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
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

    @Override
    public String toString() {
        return "{ " +
                type.get() +
                ", " + day.get().getDisplayName(TextStyle.FULL, Locale.getDefault()) +
                ", " + rotation.get() +
                ", " + time.get() +
                ", " + audience.get() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleLecture)) return false;

        ScheduleLecture that = (ScheduleLecture) o;

        if (type != null ? !type.get().equals(that.type.get()) : that.type != null) return false;
        if (day != null ? !day.get().equals(that.day.get()) : that.day != null) return false;
        if (rotation != null ? !rotation.get().equals(that.rotation.get()) : that.rotation != null) return false;
        if (time != null ? !time.get().equals(that.time.get()) : that.time != null) return false;
        return audience != null ? audience.get().equals(that.audience.get()) : that.audience == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.get().hashCode() : 0;
        result = 31 * result + (day != null ? day.get().hashCode() : 0);
        result = 31 * result + (rotation != null ? rotation.get().hashCode() : 0);
        result = 31 * result + (time != null ? time.get().hashCode() : 0);
        result = 31 * result + (audience != null ? audience.get().hashCode() : 0);
        return result;
    }

    public boolean atOneTime(ScheduleLecture lecture){
        if(day.get() == lecture.getDay() && time.get().equals(lecture.getTime())){
            if(rotation.get() == lecture.getRotation()
                    || rotation.get() == LectureRotation.EVERY_WEEK
                    || lecture.getRotation() == LectureRotation.EVERY_WEEK){
                return true;
            }
        }
        return false;
    }
}
