package root.models.enums;

public enum Attendance {
    NULL,
    ABSENT,
    LATE,
    ATTEND;

    @Override
    public String toString() {
        switch (this) {
            case NULL:
                return "-";
            case ABSENT:
                return "Відсутній";
            case LATE:
                return "Запізнився";
            case ATTEND:
                return "Є";
            default:
                return "Impossible is nothing";
        }
    }

}
