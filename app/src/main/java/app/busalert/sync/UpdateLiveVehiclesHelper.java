package app.busalert.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

public class UpdateLiveVehiclesHelper {
    public static final int UPDATE_VEHICLES_JOB_ID = 1;

    public static void startService(Context context, long intervalMilis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler =
                    (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(new JobInfo.Builder(UPDATE_VEHICLES_JOB_ID,
                    new ComponentName(context, UpdateLiveVehiclesJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build());
        }
    }

}
