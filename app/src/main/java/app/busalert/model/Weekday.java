package app.busalert.model;

public enum Weekday {
    MONDAY(1L), TUESDAY(2L), WEDNESDAY(4L), THURSDAY(8L), FRIDAY(16L), SATURDAY(32L), SUNDAY(64L);

    private long value;

    Weekday(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
