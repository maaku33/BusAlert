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
}
