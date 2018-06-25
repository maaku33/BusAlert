package app.busalert.model;

public enum VehicleType {
    BUS(0), TRAM(1), METRO(2), UNKNOWN(-1);

    private int value;

    VehicleType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static VehicleType fromInt(int value) {
        switch (value) {
            case 0:
                return BUS;
            case 1:
                return TRAM;
            case 2:
                return METRO;
            default:
                return UNKNOWN;
        }
    }
}
