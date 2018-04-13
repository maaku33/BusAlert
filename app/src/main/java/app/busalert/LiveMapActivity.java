package app.busalert;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import app.busalert.db.AppDatabase;
import app.busalert.db.entities.VehicleEntity;

public class LiveMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final double WARSAW_LAT = 52.2287;
    private static final double WARSAW_LON = 21.0036;

    private GoogleMap mMap;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng center;
        try {
            Location lastKnownLocation = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            center = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        } catch (SecurityException se) {
            center = new LatLng(WARSAW_LAT, WARSAW_LON);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 13.5f));

        final AppDatabase db = AppDatabase.getInstance(this.getApplicationContext());

        new AsyncTask<Void, Void, List<VehicleEntity>>() {
            @Override
            protected List<VehicleEntity> doInBackground(Void... voids) {
                return db.vehicleDao().loadAll();
            }

            @Override
            protected void onPostExecute(List<VehicleEntity> vehicleList) {
                for (int i = 0; i < vehicleList.size() && i < 100; i++) {
                    VehicleEntity ve = vehicleList.get(i);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(ve.getLatitude(), ve.getLongitude()))
                            .title(ve.getLine()));
                }
            }
        }.execute();

    }
}
