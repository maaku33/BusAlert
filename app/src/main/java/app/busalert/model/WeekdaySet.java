package app.busalert.model;

public class WeekdaySet {

    private long bitfield;

    public WeekdaySet() {
        this.bitfield = 0;
    }

    public WeekdaySet(long bitfield) {
        this.bitfield = bitfield;
    }

    public WeekdaySet(Weekday[] weekdays) {
        this.bitfield = 0;
        for (Weekday weekday : weekdays) {
            this.bitfield |= weekday.getValue();
        }
    }

    public Boolean contains(Weekday weekday) {
        return (bitfield & weekday.getValue()) > 0;
    }

    public void add(Weekday weekday) {
         bitfield |= weekday.getValue();
    }

    public void remove(Weekday weekday) {
         bitfield &= ~weekday.getValue();
    }

    public long toBitfield() {
        return bitfield;
    }
}
