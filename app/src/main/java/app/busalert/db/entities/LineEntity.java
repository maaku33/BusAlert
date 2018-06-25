package app.busalert.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import app.busalert.model.Line;
import app.busalert.model.VehicleType;

@Entity(tableName = "lines")
public class LineEntity implements Line {

    @NonNull
    @PrimaryKey
    private String line;
    @NonNull
    private VehicleType type;

    public LineEntity(@NonNull String line, @NonNull VehicleType type) {
        this.line = line;
        this.type = type;
    }

    @Override
    @NonNull
    public String getLine() {
        return line;
    }

    @Override
    @NonNull
    public VehicleType getType() {
        return type;
    }
}
