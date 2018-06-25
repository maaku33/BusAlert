package app.busalert.sync;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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

import app.busalert.R;
import app.busalert.db.AppDatabase;
import app.busalert.db.entities.AlertEntity;
import app.busalert.db.entities.VehicleEntity;
import app.busalert.model.Time;
import app.busalert.model.VehicleType;

public class UpdateLiveVehiclesIntentService extends IntentService {

    private static final String TAG = UpdateLiveVehiclesIntentService.class.getSimpleName();

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

    public UpdateLiveVehiclesIntentService() {
        super("update-live-vehicles-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        RequestQueue queue = Volley.newRequestQueue(context);

        LiveVehicleJsonObjectRequest busJsonRequest = new LiveVehicleJsonObjectRequest(
                BUS_LOCATION_URL, VehicleType.BUS, context);
        LiveVehicleJsonObjectRequest tramJsonRequest = new LiveVehicleJsonObjectRequest(
                TRAM_LOCATION_URL, VehicleType.TRAM, context);

        queue.add(busJsonRequest);
        queue.add(tramJsonRequest);
        Log.d(TAG, "Added requests to volley queue.");
    }

    private static List<VehicleEntity> jsonToVehicleList(JSONObject jsonObject, VehicleType type)
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

    private static class LiveVehicleJsonObjectRequest extends JsonObjectRequest {
        private static final double METER_TO_LAT = 0.000014666;
        private static final double METER_TO_LON = 0.000009009;
        private static final String CHANNEL_ID = "Live Vehicles Channel";
        private static final int METHOD = Request.Method.GET;
        private static final Response.ErrorListener ERROR_LISTENER = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle Error
            }
        };

        LiveVehicleJsonObjectRequest(String url, VehicleType type, Context context) {
            super(METHOD, url, null, getListener(type, context), ERROR_LISTENER);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Live Vehicles Channel";
                String description = "sending alert about vehicles";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }

        private static Response.Listener<JSONObject> getListener(final VehicleType type,
                                                                 final Context context) {
            return new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response) {
                    final AppDatabase database = AppDatabase.getInstance(context);

                    AsyncTask task = new AsyncTask<Void, Void, List<AlertEntity>>() {
                        @Override
                        protected List<AlertEntity> doInBackground(Void... params) {
                            try {
                                database.vehicleDao().deleteType(type);
                                database.vehicleDao().insertAll(jsonToVehicleList(response, type));
                            } catch (DataCorruptedException e) {
                                // TODO: inform user about server error
                            }

                            List<AlertEntity> result = new ArrayList<>();
                            List<AlertEntity> activeAlerts =
                                    database.alertDao().loadBetween(new Time());

                            for (AlertEntity alert : activeAlerts) {
                                /* This locating method is inaccurate since METER_TO_LAT is not constant */
                                double lat_radius = (double)alert.getRadius() * METER_TO_LAT;
                                double lon_radius = (double)alert.getRadius() * METER_TO_LON;
                                double lat = alert.getLatitude(), lon = alert.getLongitude();
                                List<VehicleEntity> vehicles = database.vehicleDao()
                                        .loadLineBetween(alert.getLine(), lat - lat_radius, lat + lat_radius,
                                                lon - lon_radius, lon + lon_radius);
                                if (!vehicles.isEmpty()) {
                                    result.add(alert);
                                }
                            }

                            return result;
                        }

                        @Override
                        protected void onPostExecute(List<AlertEntity> alerts) {
                            for (AlertEntity alert : alerts) {
                                Log.d(TAG, "Processing alert " + alert.getName() + ".");
                                NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_bus_24px)
                                        .setContentTitle(alert.getName())
                                        .setContentText(alert.getLine())
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                int notificationId = (int) alert.id;
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                notificationManager.notify(notificationId, notifBuilder.build());
                            }
                        }
                    }.execute();
                }
            };
        }
    }
}
