package app.busalert.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.busalert.db.AppDatabase;
import app.busalert.db.entities.VehicleEntity;
import app.busalert.model.VehicleType;

public class WarsawData {
    private static final String API_BASE_URL = "https://api.um.warszawa.pl/api/action";
    private static final String API_KEY_PARAM = "apikey=28c5a8ae-e0d0-4357-affd-0dfdc386ed93";
    private static final String VEHICLE_LOCATION_PARAM = "resource_id=%20f2e5503e-%20927d-4ad3-9500-4ab9e55deb59";
    private static final String VEHICLE_LOCATION_URL = API_BASE_URL + "/busestrams_get/?"
            + API_KEY_PARAM + "&" + VEHICLE_LOCATION_PARAM;

    // Returns JSON with live bus data
    private static final String BUS_LOCATION_URL = VEHICLE_LOCATION_URL + "&type=1";
    // Returns JSON with live tram data
    private static final String TRAM_LOCATION_URL = VEHICLE_LOCATION_URL + "&type=2";
    // Formatted files with data for each consecutive day
    private static final String FTP_URL = "ftp;//rozklady.ztm.waw.pl";


    public static void updateData(VehicleType vehicle, final Context context, final AppDatabase database)
            throws DataUnavailableException {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "";

        switch (vehicle) {
            case BUS:
                url = BUS_LOCATION_URL;
                break;

            case TRAM:
                url = TRAM_LOCATION_URL;
                break;

            default:
                throw new DataUnavailableException();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                try {
                                    database.vehicleDao().purgeAll();
                                    database.vehicleDao().insertAll(jsonToList(response));
                                } catch (DataCorruptedException e) {
                                    // TODO: inform user about server error
                                }
                                return null;
                            }
                        }.execute();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle Error
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private static List<VehicleEntity> jsonToList(JSONObject jsonObject)
            throws DataCorruptedException {
        List<VehicleEntity> list = new ArrayList<>();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                list.add(new VehicleEntity(jo.getString("Lines"),
                        jo.getString("Brigade"),
                        jo.getString("Time"),
                        jo.getDouble("Lat"),
                        jo.getDouble("Lon"))); // TODO: move strings to variables
            }
        } catch (JSONException e) {
            throw new DataCorruptedException();
        }

        return list;
    }
}
