package app.busalert.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import app.busalert.db.dao.VehicleDao;
import app.busalert.db.entities.VehicleEntity;

@Database(entities = {VehicleEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase sInstance;

    public abstract VehicleDao vehicleDao();

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "vehicle.db").build();
                }
            }
        }
        return sInstance;
    }
}
