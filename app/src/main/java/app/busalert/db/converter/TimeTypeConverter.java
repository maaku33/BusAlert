package app.busalert.db.converter;

import android.arch.persistence.room.TypeConverter;

import app.busalert.model.Time;

public class TimeTypeConverter {

    @TypeConverter
    public static Time toTime(long milis) {
        return new Time(milis);
    }

    @TypeConverter
    public static long toLong(Time time) {
        return time.getTimeInMillis();
    }
}
