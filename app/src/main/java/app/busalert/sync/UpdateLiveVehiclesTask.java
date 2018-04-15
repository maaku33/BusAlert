package app.busalert.sync;

import android.content.Context;
import android.os.AsyncTask;

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

public class UpdateLiveVehiclesTask extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = UpdateLiveVehiclesTask.class.getSimpleName();

    // TODO: Check if leaks occur
    // maybe no leaks occur because of dispatch from JobService?
    private final Context mApplicationContext;

    UpdateLiveVehiclesTask(Context context) {
        mApplicationContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        RequestQueue queue = Volley.newRequestQueue(mApplicationContext);

        LiveVehicleJsonObjectRequest busJsonRequest = new LiveVehicleJsonObjectRequest(
                WarsawData.BUS_LOCATION_URL, VehicleType.BUS, mApplicationContext);
        LiveVehicleJsonObjectRequest tramJsonRequest = new LiveVehicleJsonObjectRequest(
                WarsawData.TRAM_LOCATION_URL, VehicleType.TRAM, mApplicationContext);

        queue.add(busJsonRequest);
        queue.add(tramJsonRequest);

        return null;
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

                    AsyncTask task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                database.vehicleDao().deleteType(type);
                                database.vehicleDao().insertAll(jsonToList(response, type));
                            } catch (DataCorruptedException e) {
                                // TODO: inform user about server error
                            }
                            return null;
                        }
                    }.execute();
                }
            };
        }
    }

}
