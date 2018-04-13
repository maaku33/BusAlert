package app.busalert.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.busalert.db.entities.VehicleEntity;

@Dao
public interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAll(List<VehicleEntity> vehicles);

    @Delete
    public void deleteAll(List<VehicleEntity> vehicles);

    @Query("DELETE FROM vehicles")
    public void purgeAll();

    @Query("SELECT * FROM vehicles " +
            "WHERE latitude BETWEEN :latmin AND :latmax " +
            "AND longitude BETWEEN :lonmin AND :lonmax")
    public List<VehicleEntity> loadAllBetween(double latmin, double latmax, double lonmin, double lonmax);

    @Query("SELECT * FROM vehicles")
    public List<VehicleEntity> loadAll();
}
