package app.busalert.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import app.busalert.db.converter.VehicleTypeConverter;
import app.busalert.model.Vehicle;
import app.busalert.model.VehicleType;

import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

@Entity(tableName = "vehicles")
public class VehicleEntity implements Vehicle {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @TypeConverters(VehicleTypeConverter.class)
    private VehicleType type;

    @NonNull
    private String line;
    private String brigade;

    private String timestamp;
    private double latitude;
    private double longitude;

    public VehicleEntity(@NonNull VehicleType type, @NonNull String line, String brigade,
                         String timestamp, double latitude, double longitude) {
        this.type = type;
        this.line = line;
        this.brigade = brigade;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    @NonNull
    public VehicleType getType() {
        return type;
    }

    public void setType(@NonNull VehicleType type) {
        this.type = type;
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

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
