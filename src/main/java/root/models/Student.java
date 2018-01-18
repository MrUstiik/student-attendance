package root.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import root.models.enums.Attendance;
import root.models.enums.LectureType;

public class Student implements Comparable<Student> {
    private final StringProperty name;
    private ObservableMap<Lecture, ObjectProperty<Attendance>> attendance = FXCollections.observableHashMap();


    public Student() {
        name = new SimpleStringProperty("");
    }

    public Student(StringProperty name) {
        this.name = name;
    }

    public Student(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public Student(StringProperty name, ObservableMap<Lecture, ObjectProperty<Attendance>> attendance) {
        this.name = name;
        this.attendance = attendance;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObservableMap<Lecture, ObjectProperty<Attendance>> getAttendance() {
        return attendance;
    }

    public void setAttendance(ObservableMap<Lecture, ObjectProperty<Attendance>> attendance) {
        this.attendance = attendance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;

        Student student = (Student) o;

        return name.get().equals(student.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public int compareTo(Student o) {
        return getName().compareTo(o.getName());
    }

    public int getClassesCountAll(){
        return attendance.size();
    }

    public int getClassesCount(){
        return (int) attendance.values().stream().filter(attendanceObjectProperty ->
                attendanceObjectProperty.getValue() != Attendance.NULL).count();
    }

    public int getLecturesCount(){
        return (int) attendance.entrySet().stream().filter(lectureObjectPropertyEntry ->
                lectureObjectPropertyEntry.getValue().getValue() != Attendance.NULL
                        && lectureObjectPropertyEntry.getKey().getType() == LectureType.LECTURE).count();
    }

    public int getPracticalsCount(){
        return (int) attendance.entrySet().stream().filter(lectureObjectPropertyEntry ->
                lectureObjectPropertyEntry.getValue().getValue() != Attendance.NULL
                        && lectureObjectPropertyEntry.getKey().getType() == LectureType.PRACTICAL).count();
    }

    public int getAbsentCount(){
        return (int) attendance.values().stream().filter(attendanceObjectProperty ->
         attendanceObjectProperty.getValue() == Attendance.ABSENT).count();
    }

    public int getLateCount(){
        return (int) attendance.values().stream().filter(attendanceObjectProperty ->
                attendanceObjectProperty.getValue() == Attendance.LATE).count();
    }

    public int getAttendCount(){
        return (int) attendance.values().stream().filter(attendanceObjectProperty ->
                attendanceObjectProperty.getValue() == Attendance.ATTEND).count();
    }

    public int getAttendAndLateCount(){
        return (int) attendance.values().stream().filter(attendanceObjectProperty ->
                attendanceObjectProperty.getValue() == Attendance.ATTEND
                        || attendanceObjectProperty.getValue() != Attendance.LATE).count();
    }

    public int getAttendAndLateCountLecture(){
        return (int) attendance.entrySet().stream().filter(lectureObjectPropertyEntry ->
                lectureObjectPropertyEntry.getKey().getType() == LectureType.LECTURE
        && (lectureObjectPropertyEntry.getValue().getValue() == Attendance.ATTEND
                || lectureObjectPropertyEntry.getValue().getValue() == Attendance.LATE))
                .count();
    }

    public int getAttendAndLateCountPractical(){
        return (int) attendance.entrySet().stream().filter(lectureObjectPropertyEntry ->
                lectureObjectPropertyEntry.getKey().getType() == LectureType.PRACTICAL
                        && (lectureObjectPropertyEntry.getValue().getValue() == Attendance.ATTEND
                        || lectureObjectPropertyEntry.getValue().getValue() == Attendance.LATE))
                .count();
    }
}
