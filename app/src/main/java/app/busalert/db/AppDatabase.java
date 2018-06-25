package app.busalert.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import app.busalert.db.converter.TimeTypeConverter;
import app.busalert.db.converter.VehicleTypeConverter;
import app.busalert.db.converter.WeekdaySetTypeConverter;
import app.busalert.db.dao.AlertDao;
import app.busalert.db.dao.LineDao;
import app.busalert.db.dao.VehicleDao;
import app.busalert.db.entities.AlertEntity;
import app.busalert.db.entities.LineEntity;
import app.busalert.db.entities.VehicleEntity;

@Database(entities = {VehicleEntity.class, AlertEntity.class, LineEntity.class},
        version = 9, exportSchema = false)
@TypeConverters({VehicleTypeConverter.class, WeekdaySetTypeConverter.class, TimeTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase sInstance;

    public abstract VehicleDao vehicleDao();

    public abstract AlertDao alertDao();

    public abstract LineDao lineDao();

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "vehicle.db")
                            .fallbackToDestructiveMigration() // TODO: manage better migrations
                            .build();
                }
            }
        }
        return sInstance;
    }
}
