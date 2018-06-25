package app.busalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.util.List;

import app.busalert.db.AppDatabase;
import app.busalert.db.dao.AlertDao;
import app.busalert.db.entities.AlertEntity;
import app.busalert.model.VehicleType;
import app.busalert.sync.UpdateLiveVehiclesIntentService;
import app.busalert.views.AlertBox;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final long ALARM_INTERVAL_SHORT_MILIS = 15L * 1000L;
    private static final long ALARM_INTERVAL_LONG_MILIS = 90L * 1000L;

    private AlarmManager alarmManager;
    private PendingIntent updateAlarmIntent;

    private LinearLayout mAlertList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlertCreationActivity();
            }
        });

        mAlertList = findViewById(R.id.alert_list);
        updateAlertBoxes();

        setupAlarmManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAlertBoxes();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        updateAlertBoxes();
    }

    @Override
    public void onDestroy() {
        alarmManager.cancel(updateAlarmIntent);
        super.onDestroy();
    }

    private void updateAlertBoxes() {
        new UpdateAlertBoxesTask(getDatabase(), mAlertList, this).execute();
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

    private void setupAlarmManager() {
        Context context = getApplicationContext();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, UpdateLiveVehiclesIntentService.class);
        updateAlarmIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 4 * 1000,
                ALARM_INTERVAL_SHORT_MILIS,
                updateAlarmIntent);
    }

    public void deleteAlert(View view) {
        final AlertBox alertBox = (AlertBox) view.getParent().getParent();

        new Thread() {
            @Override
            public void run() {
                getDatabase().alertDao().delete(alertBox.getAlertId());
                updateAlertBoxes();
            }
        }.start();
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

    private static class UpdateAlertBoxesTask extends AsyncTask<Void, Void, List<AlertEntity>> {

        private AlertDao dao;
        private WeakReference<LinearLayout> alertListRef;
        private WeakReference<Context> contextRef;

        UpdateAlertBoxesTask(AppDatabase database, LinearLayout alertList, Context context) {
            this.dao = database.alertDao();
            this.alertListRef = new WeakReference<>(alertList);
            this.contextRef = new WeakReference<>(context);
        }

        @Override
        protected List<AlertEntity> doInBackground(Void... voids) {
            return dao.loadAll();
        }

        @Override
        protected void onPostExecute(List<AlertEntity> alerts) {
            final LinearLayout alertList = alertListRef.get();
            final Context context = contextRef.get();
            if (alertList == null || context == null) return;

            alertList.removeAllViews();
            for (AlertEntity alert : alerts) {
                AlertBox box = new AlertBox(context, null, alert.id, VehicleType.BUS,
                        alert.getLine(), alert.getName(), alert.getIntervalStart(),
                        alert.getIntervalEnd());
                alertList.addView(box);
            }

            alertList.invalidate();
        }
    }
}
