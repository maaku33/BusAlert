package app.busalert;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.Locale;

import app.busalert.db.AppDatabase;
import app.busalert.db.entities.AlertEntity;
import app.busalert.model.Time;
import app.busalert.model.WeekdaySet;

public class AlertCreationActivity extends MapFragmentActivity implements GoogleMap.OnMapClickListener {

    private static final int SEEK_BAR_MAX = 2000;

    private EditText mName, mLine;
    private Button mStart, mEnd;
    private TextView mSeekBarTextView;
    private SeekBar mSeekBar;
    private Marker mMarker;
    private Circle mCircle;
    private Time start, end;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    finish();
                    return true;
                case R.id.navigation_save:
                    if (!saveAlert()) {
                        Snackbar.make(findViewById(R.id.container),
                                getString(R.string.message_invalid_parameters),
                                Snackbar.LENGTH_LONG).show();
                        return true;
                    }

                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_creation);

        mName = findViewById(R.id.new_alert_name);
        mLine = findViewById(R.id.new_alert_line);
        mStart = findViewById(R.id.new_alert_start_time);
        mEnd = findViewById(R.id.new_alert_end_time);
        mSeekBar = findViewById(R.id.new_alert_radius);
        mSeekBarTextView = findViewById(R.id.radius_text_view);

        mSeekBarTextView.setText("0 m");
        mSeekBar.setMax(SEEK_BAR_MAX);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSeekBarTextView.setText(String.format(Locale.getDefault(), "%1$d m", i));

                if (mCircle != null) {
                    mCircle.remove();
                }

                CircleOptions circle = new CircleOptions();
                circle.center(mMarker.getPosition());
                circle.radius(i);
                circle.strokeColor(Color.RED);
                mCircle = mMap.addCircle(circle);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.new_alert_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReadyMoved() {
        LatLng position = mMap.getCameraPosition().target;
        mMarker = mMap.addMarker(new MarkerOptions().position(position));
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        mMarker.remove();
        mMarker = mMap.addMarker(new MarkerOptions().position(point));

        if (mCircle != null) {
            mCircle.setCenter(point);
        }
    }

    /**
     * If filled out views contain valid parameters then tries to add new
     * alert to the database and returns true. Otherwise returns false.
     * @return true if all alert parameters valid, otherwise false
     */
    private boolean saveAlert() {
        final String name = mName.getText().toString();
        final String line = mLine.getText().toString();
        final double lat = mMarker.getPosition().latitude, lon = mMarker.getPosition().longitude;
        final long radius = mSeekBar.getProgress();
        final AppDatabase db = AppDatabase.getInstance(this.getApplicationContext());

        if (start == null || end == null || radius == 0) {
            return false;
        }

        new Thread() {
            @Override
            public void run() {
                // Since no weekday parameter available creates alert active
                // entire week.
                db.alertDao().insert(new AlertEntity(name, line, lat, lon, radius,
                        new WeekdaySet(~new WeekdaySet().toBitfield()), start, end));
            }
        }.start();

        return true;
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "time picker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            AlertCreationActivity act = (AlertCreationActivity) getActivity();
            act.start = new Time(hourOfDay, minute);
            act.mStart.setText(act.start.toString());
        }
    }

    public void showTimePickerDialogEnd(View v) {
        DialogFragment newFragment = new TimePickerFragmentEnd();
        newFragment.show(getFragmentManager(), "time picker");
    }

    public static class TimePickerFragmentEnd extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            AlertCreationActivity act = (AlertCreationActivity) getActivity();
            act.end = new Time(hourOfDay, minute);
            act.mEnd.setText(act.end.toString());
        }
    }

}
