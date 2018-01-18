package root.models.enums;

public enum LectureRotation {
    FIRST_WEEK,
    SECOND_WEEK,
    EVERY_WEEK;

    @Override
    public String toString() {
        switch (this) {
            case FIRST_WEEK:
                return "Перший тиждень";
            case SECOND_WEEK:
                return "Другий тиждень";
            case EVERY_WEEK:
                return "Щотижня";
            default:
                return "Impossible is nothing";
        }
    }
}
