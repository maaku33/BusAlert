package app.busalert.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import app.busalert.model.Vehicle;
import android.support.annotation.NonNull;

@Entity(tableName = "vehicles")
public class VehicleEntity implements Vehicle {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    private String line;
    private String brigade;

    private String timestamp;
    private long latitude;
    private long longitude;

    public VehicleEntity(@NonNull String line, String brigade, String timestamp, long latitude,
                         long longitude) {
        this.line = line;
        this.brigade = brigade;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    @NonNull
    public String getLine() {
        return line;
    }

    public void setLine(@NonNull String line) {
        this.line = line;
    }

    @Override
    public String getBrigade() {
        return brigade;
    }

    public void setBrigade(String brigade) {
        this.brigade = brigade;
    }

    @Override
    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    @Override
    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }
}
