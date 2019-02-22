package com.example.android.locationbatterylogger;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

/**
 * Handles incoming location updates.
 */

public class LocationUpdatesIntentService extends IntentService {

    private static final String TAG = "LocationUpdatesIntentService";
    static final String ACTION_PROCESS_UPDATES = "com.example.android.locationbatterylogger.action.PROCESS_UPDATES";

    public LocationUpdatesIntentService() {
        // Name the worker thread.
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    if (locations.isEmpty()) {
                        LocationLog.getInstance(this).logLocation("0", "0");
                    } else {
                        Location location = locations.get(0);
                        LocationLog.getInstance(this).logLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                    }
                }
            }
        }
    }
}
