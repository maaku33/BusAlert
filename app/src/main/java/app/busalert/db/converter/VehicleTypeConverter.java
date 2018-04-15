package app.busalert.db.converter;

import android.arch.persistence.room.TypeConverter;

import app.busalert.model.VehicleType;

public class VehicleTypeConverter {

    @TypeConverter
    public static VehicleType toVehicleType(int value) {
        if (value == VehicleType.BUS.getValue()) {
            return VehicleType.BUS;
        } else if (value == VehicleType.TRAM.getValue()) {
            return VehicleType.TRAM;
        } else if (value == VehicleType.METRO.getValue()) {
            return VehicleType.METRO;
        } else {
            return VehicleType.UNKNOWN;
        }
    }

    @TypeConverter
    public static int toInt(VehicleType type) {
        return type.getValue();
    }
}
