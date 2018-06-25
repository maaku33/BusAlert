package app.busalert;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public abstract class MapFragmentActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final double WARSAW_LAT = 52.2287;
    private static final double WARSAW_LON = 21.0036;
    private static final float START_ZOOM = 13.5f;

    protected GoogleMap mMap;

    /**
     * Called when map is ready and camera position is set.
     */
    protected abstract void onMapReadyMoved();

    /**
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LatLng position;
        try {
            if (locationManager == null) throw new SecurityException();
            Location lastKnownLocation = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            position = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        } catch (SecurityException se) {
            position = new LatLng(WARSAW_LAT, WARSAW_LON);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, START_ZOOM));
        onMapReadyMoved();
    }
}
