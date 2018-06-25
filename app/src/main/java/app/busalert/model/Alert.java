package app.busalert.model;

public interface Alert {

    String getName();
    String getLine();
    double getLatitude();
    double getLongitude();
    long getRadius();
    WeekdaySet getWeekdaySet();
    Time getIntervalStart();
    Time getIntervalEnd();

}
