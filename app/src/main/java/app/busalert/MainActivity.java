package app.busalert;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.List;

import app.busalert.db.AppDatabase;
import app.busalert.db.entities.VehicleEntity;
import app.busalert.model.VehicleType;
import app.busalert.network.DataUnavailableException;
import app.busalert.network.WarsawData;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mAlertList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlertCreationActivity();
            }
        });

        mAlertList = (LinearLayout) findViewById(R.id.alert_list);

        try {
            WarsawData.updateData(VehicleType.BUS, this.getApplicationContext(), getDatabase());
        } catch (DataUnavailableException e) {
            e.printStackTrace(); // shouldn't happen for buses
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        drawAlerts();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        drawAlerts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflating the menu. Adds items to the action bar
        // if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button,
        // utilizing parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                startSettingsActivity();
                return true;

            case R.id.action_live_map:
                startLiveMapActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private AppDatabase getDatabase() {
        return AppDatabase.getInstance(this.getApplicationContext());
    }

    private void startAlertCreationActivity() {
        startActivity(new Intent(this.getApplicationContext(), AlertCreationActivity.class));
    }

    private void startSettingsActivity() {
        startActivity(new Intent(this.getApplicationContext(), SettingsActivity.class));
    }

    private void startLiveMapActivity() {
        startActivity(new Intent(this.getApplicationContext(), LiveMapActivity.class));
    }

    private void drawAlerts() {

    }
}
