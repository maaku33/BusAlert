package app.busalert.network;

import org.json.JSONObject;

import app.busalert.model.UnavaliableDataException;
import app.busalert.model.VehicleType;

public class WarsawData {
    private static final String API_BASE_URL = "https://api.um.warszawa.pl/api/action";
    private static final String API_KEY_PARAM = "apikey=28c5a8ae-e0d0-4357-affd-0dfdc386ed93";
    private static final String VEHICLE_LOCATION_PARAM = "resource_id=%20f2e5503e-%20927d-4ad3-9500-4ab9e55deb59";
    private static final String VEHICLE_LOCATION_URL = API_BASE_URL + "/busestrams_get/?"
            + API_KEY_PARAM + "&" + VEHICLE_LOCATION_PARAM;

    // Returns JSON with live bus data
    public static final String BUS_LOCATION_URL = VEHICLE_LOCATION_URL + "&type=1";
    // Returns JSON with live tram data
    public static final String TRAM_LOCATION_URL = VEHICLE_LOCATION_URL + "&type=2";
    // Formatted files with data for each consecutive day
    public static final String FTP_URL = "ftp;//rozklady.ztm.waw.pl";

    public JSONObject getLiveData(VehicleType vehicle) throws UnavaliableDataException {
        switch (vehicle) {
            case BUS:

            case TRAM:

            default:
                throw new UnavaliableDataException();
        }
    }
}
