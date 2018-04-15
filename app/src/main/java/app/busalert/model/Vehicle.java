package app.busalert.model;

public interface Vehicle {

    VehicleType getType();
    String getLine();
    String getBrigade();
    double getLatitude();
    double getLongitude();
}
