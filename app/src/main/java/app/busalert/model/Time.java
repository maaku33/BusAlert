package app.busalert.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Time extends GregorianCalendar {

    private static final int YEAR = 1970;
    private static final int MONTH = 0;
    private static final int DATE = 1;

    public Time() {
        super();  this.set(YEAR, MONTH, DATE);
    }

    public Time(long milis) {
        super();
        this.setTimeInMillis(milis);
    }

    public Time(int hour, int minute) {
        super(YEAR, MONTH, DATE, hour, minute);
    }

    public String toString() {
        return this.getTime().getHours() + ":" + this.getTime().getMinutes();
    }

}
