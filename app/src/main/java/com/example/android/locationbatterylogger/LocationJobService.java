package com.example.android.locationbatterylogger;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

/**
 * To be scheduled by the JobScheduler
 * Start another service: LocationUpdateIntentService.
 */
public class LocationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent service = new Intent(getApplicationContext(), LocationUpdatesIntentService.class);
        getApplicationContext().startService(service);
        Utils.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
