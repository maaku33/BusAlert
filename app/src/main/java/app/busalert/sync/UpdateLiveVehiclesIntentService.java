package app.busalert.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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
import app.busalert.network.DataCorruptedException;
import app.busalert.network.WarsawData;

public class UpdateLiveVehiclesIntentService extends IntentService {

    private static final String TAG = UpdateLiveVehiclesIntentService.class.getSimpleName();

    public UpdateLiveVehiclesIntentService() {
        super("update-live-vehicles-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service created");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        RequestQueue queue = Volley.newRequestQueue(context);

        UpdateLiveVehiclesTask.LiveVehicleJsonObjectRequest busJsonRequest = new UpdateLiveVehiclesTask.LiveVehicleJsonObjectRequest(
                WarsawData.BUS_LOCATION_URL, VehicleType.BUS, context);
        UpdateLiveVehiclesTask.LiveVehicleJsonObjectRequest tramJsonRequest = new UpdateLiveVehiclesTask.LiveVehicleJsonObjectRequest(
                WarsawData.TRAM_LOCATION_URL, VehicleType.TRAM, context);

        queue.add(busJsonRequest);
        queue.add(tramJsonRequest);

        Log.i(TAG, "Added requests to volley queue");
    }

    private static List<VehicleEntity> jsonToList(JSONObject jsonObject, VehicleType type)
            throws DataCorruptedException {
        List<VehicleEntity> list = new ArrayList<>();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                list.add(new VehicleEntity(type,
                        jo.getString("Lines"),
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

    protected static class LiveVehicleJsonObjectRequest extends JsonObjectRequest {
        private static final int METHOD = Request.Method.GET;
        private static final Response.ErrorListener ERROR_LISTENER = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle Error
            }
        };

        LiveVehicleJsonObjectRequest(String url, VehicleType type, Context context) {
            super(METHOD, url, null, getListener(type, context), ERROR_LISTENER);
        }

        private static Response.Listener<JSONObject> getListener(final VehicleType type,
                                                                 final Context context) {
            return new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response) {
                    final AppDatabase database = AppDatabase.getInstance(context);

                        try {
                            database.vehicleDao().deleteType(type);
                            database.vehicleDao().insertAll(jsonToList(response, type));
                        } catch (DataCorruptedException e) {
                            // TODO: inform user about server error
                        }
                }
            };
        }
    }
}
