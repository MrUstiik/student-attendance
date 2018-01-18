package root.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Subject {
    private final StringProperty title;
    private final StringProperty lecturer;
    private final StringProperty practicalLecturer;
    private final StringProperty group;

    public Subject() {
        this(new SimpleStringProperty(""), new SimpleStringProperty(""),
                new SimpleStringProperty(""), new SimpleStringProperty(""));
    }

    public Subject(StringProperty title, StringProperty lecturer, StringProperty practicalLecturer, StringProperty group) {
        this.title = title;
        this.lecturer = lecturer;
        this.practicalLecturer = practicalLecturer;
        this.group = group;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getLecturer() {
        return lecturer.get();
    }

    public StringProperty lecturerProperty() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer.set(lecturer);
    }

    public String getPracticalLecturer() {
        return practicalLecturer.get();
    }

    public StringProperty practicalLecturerProperty() {
        return practicalLecturer;
    }

    public void setPracticalLecturer(String practicalLecturer) {
        this.practicalLecturer.set(practicalLecturer);
    }

    public String getGroup() {
        return group.get();
    }

    public StringProperty groupProperty() {
        return group;
    }

    public void setGroup(String group) {
        this.group.set(group);
    }

    @Override
    public String toString() {
        return title.get() + "(" + lecturer.get() + "," + group.get() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;

        Subject subject = (Subject) o;

        if (title != null ? !title.equals(subject.title) : subject.title != null) return false;
        if (lecturer != null ? !lecturer.equals(subject.lecturer) : subject.lecturer != null) return false;
        return group != null ? group.equals(subject.group) : subject.group == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (lecturer != null ? lecturer.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }

    public void clear(){
        title.setValue("");
        lecturer.setValue("");
        practicalLecturer.setValue("");
        group.setValue("");
    }
}
