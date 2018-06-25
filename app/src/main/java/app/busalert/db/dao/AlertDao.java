package app.busalert.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.busalert.db.entities.AlertEntity;
import app.busalert.model.Time;

@Dao
public interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AlertEntity alert);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AlertEntity> alerts);

    @Delete
    void deleteAll(List<AlertEntity> alerts);

    @Delete
    void delete(AlertEntity alert);

    @Query("DELETE FROM alerts WHERE id = :id")
    void delete(long id);

    @Query("DELETE FROM alerts")
    void purgeAll();

    @Query("SELECT * FROM alerts")
    List<AlertEntity> loadAll();

    @Query("SELECT * FROM alerts WHERE intervalStart <= :time AND :time <= intervalEnd")
    List<AlertEntity> loadBetween(Time time);
}
