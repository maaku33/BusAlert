package app.busalert;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import app.busalert.db.AppDatabase;
import app.busalert.db.entities.AlertEntity;
import app.busalert.db.entities.VehicleEntity;

public class LiveMapActivity extends MapFragmentActivity implements GoogleMap.OnMarkerClickListener {
    private static final String TAG = LiveMapActivity.class.getSimpleName();

    private static final Integer VEHICLE_MARKER_TAG = 0;
    private static final Integer ALERT_MARKER_TAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_map);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            NavUtils.navigateUpFromSameTask(this);
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReadyMoved() {
        mMap.setOnMarkerClickListener(this);
        new DrawAlertsTask(AppDatabase.getInstance(this.getApplicationContext()), mMap, this).execute();
        new DrawVehicleTask(AppDatabase.getInstance(this.getApplicationContext()), mMap, this).execute();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        MarkerTag tag = (MarkerTag) marker.getTag();
        if (tag == null) {
            Log.d(TAG, "Marker tag not set!");
            return false;
        }

        if (tag.tag == ALERT_MARKER_TAG) {
            CircleOptions circle = new CircleOptions();
            circle.center(marker.getPosition());
            circle.radius(tag.radius);
            circle.strokeColor(Color.RED);
            mMap.addCircle(circle);
        } else if (tag.tag == VEHICLE_MARKER_TAG) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            return true;
        }

        return false;
    }

    private static class MarkerTag {
        final int tag;
        final long radius;

        MarkerTag(int tag) {
            this(tag, 0);
        }

        MarkerTag(int tag, long radius) {
            this.tag = tag;
            this.radius = radius;
        }
    }

    private abstract static class DrawTask<T> extends AsyncTask<Void, Void, T> {
        final AppDatabase database;
        final GoogleMap mMap;

        DrawTask(AppDatabase database, GoogleMap map) {
            this.mMap = map;
            this.database = database;
        }

        protected abstract T query();
        protected abstract void draw(T queryResult);

        @Override
        protected T doInBackground(Void... voids) {
            return query();
        }

        @Override
        protected void onPostExecute(T queryResult) {
            draw(queryResult);
        }
    }

    private static class DrawAlertsTask extends DrawTask<List<AlertEntity>> {
        private Bitmap radarBitmap;

        DrawAlertsTask(AppDatabase database, GoogleMap map, Context context) {
            super(database, map);
            this.radarBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_radar);
        }

        @Override
        protected List<AlertEntity> query() {
            return database.alertDao().loadAll();
        }

        @Override
        protected void draw(List<AlertEntity> alertList) {
            Marker marker;
            for (AlertEntity alert : alertList) {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(alert.getLatitude(), alert.getLongitude()))
                        .title(alert.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(radarBitmap)));
                marker.setTag(new MarkerTag(ALERT_MARKER_TAG, alert.getRadius()));
            }
        }
    }

    private static class DrawVehicleTask extends DrawTask<List<VehicleEntity>> {
        private Bitmap vehicleBitmap;
        private TextPaint textPaint;
        private float density;

        DrawVehicleTask(AppDatabase database, GoogleMap map, Context context) {
            super(database, map);
            this.vehicleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_vehicle);
            this.density = context.getResources().getDisplayMetrics().density;

            this.textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(24.0f * density);
        }

        @Override
        protected List<VehicleEntity> query() {
            return database.vehicleDao().loadAll();
        }

        @Override
        protected void draw(List<VehicleEntity> vehicleList) {
            Marker marker;
            for (int i = 0; i < vehicleList.size() && i < 400; i++) {
                VehicleEntity ve = vehicleList.get(i);

                Bitmap bmp = vehicleBitmap.copy(vehicleBitmap.getConfig(), true);
                Canvas canvas = new Canvas(bmp);
                canvas.drawText(ve.getLine(), 2.5f * density, 22.0f * density, textPaint);

                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(ve.getLatitude(), ve.getLongitude()))
                        .title(ve.getLine())
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                marker.setTag(new MarkerTag(VEHICLE_MARKER_TAG));
            }
        }
    }
}
