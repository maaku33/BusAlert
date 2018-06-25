package app.busalert.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.busalert.db.entities.VehicleEntity;
import app.busalert.model.Vehicle;
import app.busalert.model.VehicleType;

@Dao
public interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<VehicleEntity> vehicles);

    @Delete
    void deleteAll(List<VehicleEntity> vehicles);

    @Query("DELETE FROM vehicles")
    void purgeAll();

    @Query("DELETE FROM vehicles WHERE type = :type")
    void deleteType(VehicleType type);

    @Query("SELECT * FROM vehicles " +
            "WHERE latitude BETWEEN :latmin AND :latmax " +
            "AND longitude BETWEEN :lonmin AND :lonmax")
    List<VehicleEntity> loadAllBetween(double latmin, double latmax,
                                       double lonmin, double lonmax);

    @Query("SELECT * FROM vehicles WHERE line = :line " +
            "AND latitude BETWEEN :latmin AND :latmax " +
            "AND longitude BETWEEN :lonmin AND :lonmax")
    List<VehicleEntity> loadLineBetween(String line, double latmin, double latmax,
                                        double lonmin, double lonmax);

    @Query("SELECT * FROM vehicles WHERE line = :line")
    List<VehicleEntity> loadAllFromLine(String line);

    @Query("SELECT DISTINCT line FROM vehicles")
    List<String> loadAllLines();

    @Query("SELECT * FROM vehicles")
    List<VehicleEntity> loadAll();
}
