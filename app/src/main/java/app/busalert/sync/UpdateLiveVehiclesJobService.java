package app.busalert.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class UpdateLiveVehiclesJobService extends JobService {
    private UpdateLiveVehiclesTask mUpdateLiveVehiclesTask = null;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mUpdateLiveVehiclesTask = new UpdateLiveVehiclesTask(this.getApplicationContext()) {
            @Override
            protected void onPostExecute(Void param) {
                super.onPostExecute(param);
                jobFinished(jobParameters, false);
            }
        };
        mUpdateLiveVehiclesTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mUpdateLiveVehiclesTask != null) {
            mUpdateLiveVehiclesTask.cancel(true);
        }
        return true;
    }
}



