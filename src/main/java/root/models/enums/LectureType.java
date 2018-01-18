package root.models.enums;

public enum LectureType {
    LECTURE,
    PRACTICAL;

    @Override
    public String toString() {
        switch (this) {
            case LECTURE:
                return "Лекція";
            case PRACTICAL:
                return "Практика";
            default:
                return "Impossible is nothing";
        }
    }
}
