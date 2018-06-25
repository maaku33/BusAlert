package app.busalert.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.busalert.db.entities.LineEntity;
import app.busalert.model.VehicleType;

@Dao
public interface LineDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(LineEntity line);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<LineEntity> lines);

    @Query("DELETE FROM lines")
    void purgeAll();

    @Query("SELECT * FROM lines WHERE type = :type")
    List<LineEntity> loadAllOfType(VehicleType type);

    @Query("SELECT * FROM lines")
    List<LineEntity> loadAll();
}
