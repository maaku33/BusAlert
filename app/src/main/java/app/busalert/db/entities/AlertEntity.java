package app.busalert.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import app.busalert.db.converter.TimeTypeConverter;
import app.busalert.db.converter.WeekdaySetTypeConverter;
import app.busalert.model.Alert;
import app.busalert.model.Time;
import app.busalert.model.WeekdaySet;

@Entity(tableName = "alerts")
public class AlertEntity implements Alert {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    private String name;

    @NonNull
    private String line;
    private double latitude;
    private double longitude;
    private long radius;
    @NonNull
    @TypeConverters(WeekdaySetTypeConverter.class)
    private WeekdaySet weekdaySet;
    @NonNull
    @TypeConverters(TimeTypeConverter.class)
    private Time intervalStart;
    @NonNull
    @TypeConverters(TimeTypeConverter.class)
    private Time intervalEnd;

    public AlertEntity(@NonNull String name, @NonNull String line, double latitude, double longitude,
                       long radius, @NonNull WeekdaySet weekdaySet, @NonNull Time intervalStart,
                       @NonNull Time intervalEnd) {
        this.name = name;
        this.line = line;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.weekdaySet = weekdaySet;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setLine(@NonNull String line) {
        this.line = line;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }

    public void setWeekdaySet(@NonNull WeekdaySet weekdaySet) {
        this.weekdaySet = weekdaySet;
    }

    public void setIntervalStart(@NonNull Time intervalStart) {
        this.intervalStart = intervalStart;
    }

    public void setIntervalEnd(@NonNull Time intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getLine() {
        return line;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getRadius() {
        return radius;
    }

    @NonNull
    public WeekdaySet getWeekdaySet() {
        return weekdaySet;
    }

    @NonNull
    public Time getIntervalStart() {
        return intervalStart;
    }

    @NonNull
    public Time getIntervalEnd() {
        return intervalEnd;
    }

}
