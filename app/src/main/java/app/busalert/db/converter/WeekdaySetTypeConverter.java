package app.busalert.db.converter;

import android.arch.persistence.room.TypeConverter;

import app.busalert.model.WeekdaySet;

public class WeekdaySetTypeConverter {

    @TypeConverter
    public static WeekdaySet toWeekdaySet(long bitfield) {
        return new WeekdaySet(bitfield);
    }

    @TypeConverter
    public static long toLong(WeekdaySet weekdaySet) {
        return weekdaySet.toBitfield();
    }
}
