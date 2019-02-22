package com.example.android.locationbatterylogger;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class Utils {

    private static int minInterval = 30;
    private static int maxInterval = 60;
    /**
     * Using JobScheduler to start the location update service every min to max seconds
     */
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, LocationUpdatesIntentService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(minInterval * 1000);  // wait at least
        builder.setOverrideDeadline(maxInterval * 1000);  // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not

        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
