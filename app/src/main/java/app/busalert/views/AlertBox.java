package app.busalert.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.busalert.R;
import app.busalert.model.Time;
import app.busalert.model.VehicleType;

public class AlertBox extends RelativeLayout {

    private long id;
    private ImageView mIcon, mDelete;
    private TextView mLine, mName, mTime;

    public AlertBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlertBox(Context context, @Nullable AttributeSet attrs, long id, VehicleType vehicleType,
                    String line, String name, Time start, Time end) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.fragment_alert_box, this);
        mIcon = findViewById(R.id.alert_box_icon);
        mLine = findViewById(R.id.alert_box_line);
        mName = findViewById(R.id.alert_box_name);
        mTime = findViewById(R.id.alert_box_time);
        mDelete = findViewById(R.id.alert_box_delete);

        this.id = id;
        mLine.setText(line);
        mName.setText(name);
        mTime.setText("from " + start.toString() + " to " + end.toString());
        if (vehicleType == VehicleType.BUS) {
            mIcon.setImageResource(R.drawable.ic_bus_24px);
        } else if (vehicleType == VehicleType.TRAM) {
            mIcon.setImageResource(R.drawable.ic_tram_24px);
        }
        mDelete.setImageResource(R.drawable.ic_delete_forever_black_24dp);
    }

    public long getAlertId() {
        return id;
    }

}
