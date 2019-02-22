package com.example.android.locationbatterylogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

/**
 * Receiver for handling location updates.
 */

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "LocationUpdatesBroadcastReceiver";
    static final String ACTION_PROCESS_UPDATES = "com.example.android.locationbatterylogger.action.PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    if (locations.isEmpty()) {
                        LocationLog.getInstance(context).logLocation("0", "0");
                    } else {
                        Location location = locations.get(0);
                        LocationLog.getInstance(context).logLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                    }
//                    Utils.sendNotification(context, Utils.getLocationResultTitle(context, locations));
//                    Log.d(TAG, Utils.getLocationUpdatesResult(context));
                    Utils.scheduleJob(context);
                }
            }
        }
    }
}
