package com.example.android.locationbatterylogger;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SHARED_PREF = "shared_preferences";
    private static final String RUNNING = "location_update_running";
    private static final String LOCATION_UPDATE_INTERVAL = "location_update_interval";
    private static final String LOCATION_FASTEST_UPDATE_INTERVAL = "location_update_fastest_interval";
    private static final String LOCATION_UPDATE_MAX_WAIT = "location_update_max_wait";

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
//    private static final long UPDATE_INTERVAL = 60000; // Every 60 seconds.
//
//    /**
//     * The fastest rate for active location updates. Updates will never be more frequent
//     * than this value, but they may be less frequent.
//     */
//    private static final long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 seconds
//
//    /**
//     * The max time before batched results are delivered by location services. Results may be
//     * delivered sooner than this interval.
//     */
//    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    // UI Widgets.
    private Button mRequestUpdatesButton;
    private Button mRemoveUpdatesButton;
    private Button mViewLocationLogButton;
    private EditText mLocationIntervalEditText;
    private EditText mLocationFastestIntervalEditText;
    private EditText mLocationMaxWaitEditText;

    SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPref = getApplicationContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        mRequestUpdatesButton = (Button) findViewById(R.id.button_requestupdate);
        mRemoveUpdatesButton = (Button) findViewById(R.id.button_removeupdate);
        mViewLocationLogButton = (Button) findViewById(R.id.button_viewlog);
        mLocationIntervalEditText = (EditText) findViewById(R.id.edittext_locationupdateinterval);
        mLocationFastestIntervalEditText = (EditText) findViewById(R.id.edittext_locationfastestupdateinterval);
        mLocationMaxWaitEditText = (EditText) findViewById(R.id.edittext_locationupdatemaxwait);

        // Runtime permissions for locations.
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSharedPref.contains(RUNNING) && mSharedPref.getBoolean(RUNNING, false)) {
            // The service is running to check location updates.
            mRequestUpdatesButton.setEnabled(false);
            mRemoveUpdatesButton.setEnabled(true);
        } else {
            mRequestUpdatesButton.setEnabled(true);
            mRemoveUpdatesButton.setEnabled(false);
        }
        if (mSharedPref.contains(LOCATION_UPDATE_INTERVAL)) {
            mLocationIntervalEditText.setText(String.valueOf(mSharedPref.getLong(LOCATION_UPDATE_INTERVAL, 0)));
        }
        if (mSharedPref.contains(LOCATION_FASTEST_UPDATE_INTERVAL)) {
            mLocationFastestIntervalEditText.setText(String.valueOf(mSharedPref.getLong(LOCATION_FASTEST_UPDATE_INTERVAL, 0)));
        }
        if (mSharedPref.contains(LOCATION_UPDATE_MAX_WAIT)) {
            mLocationMaxWaitEditText.setText(String.valueOf(mSharedPref.getLong(LOCATION_UPDATE_MAX_WAIT, 0)));
        }
    }

    private void createLocationRequest(long updateInterval, long fastestUpdateInterval, long maxWaitTime) {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        // Note: apps running on "O" devices (regardless of targetSdkVersion) may receive updates
        // less frequently than this interval when the app is no longer in the foreground.
        mLocationRequest.setInterval(updateInterval);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(fastestUpdateInterval);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest.setMaxWaitTime(maxWaitTime);
    }

    private PendingIntent getPendingIntent() {
        // Note: for apps targeting API level 25 ("Nougat") or lower, either
        // PendingIntent.getService() or PendingIntent.getBroadcast() may be used when requesting
        // location updates. For apps targeting API level O, only
        // PendingIntent.getBroadcast() should be used. This is due to the limits placed on services
        // started in the background in "O".

        // TODO(developer): uncomment to use PendingIntent.getService().
//        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
//        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
            } else {
                // Permission denied.
                Toast.makeText(MainActivity.this, "Location Permission must be granted to use this app.", Toast.LENGTH_LONG).show();
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
//                Snackbar.make(
//                        findViewById(R.id.activity_main),
//                        R.string.permission_denied_explanation,
//                        Snackbar.LENGTH_INDEFINITE)
//                        .setAction(R.string.settings, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                // Build intent that displays the App settings screen.
//                                Intent intent = new Intent();
//                                intent.setAction(
//                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package",
//                                        BuildConfig.APPLICATION_ID, null);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        })
//                        .show();
            }
        }
    }

    /**
     * Handles the Request Updates button and requests start of location updates.
     */
    public void requestLocationUpdates(View view) {

        long locationUpdateInterval = 0;
        long locationFastestUpdateInterval = 0;
        long locationUpdateMaxWait = 0;

        try {
            Log.i(TAG, "Starting location updates");
            //            Utils.setRequestingLocationUpdates(this, true)

            String locationIntervalStr = mLocationIntervalEditText.getText().toString();
            String locationFastestIntervalStr = mLocationFastestIntervalEditText.getText().toString();
            String locationMaxWaitStr = mLocationMaxWaitEditText.getText().toString();
            if (locationIntervalStr.isEmpty() || locationFastestIntervalStr.isEmpty() || locationMaxWaitStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please set all the location update intervals", Toast.LENGTH_LONG).show();
            } else {
                locationUpdateInterval = 1000 * Integer.valueOf(locationIntervalStr);  // as in milliseconds
                locationFastestUpdateInterval = 1000 * Integer.valueOf(locationFastestIntervalStr);
                locationUpdateMaxWait = 1000 * Integer.valueOf(locationMaxWaitStr);
                createLocationRequest(locationUpdateInterval, locationFastestUpdateInterval, locationUpdateMaxWait);
            }

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
            mRequestUpdatesButton.setEnabled(false);
            mRemoveUpdatesButton.setEnabled(true);

            mSharedPref.edit().putBoolean(RUNNING, true).commit();
            mSharedPref.edit().putLong(LOCATION_UPDATE_INTERVAL, locationUpdateInterval / 1000).commit();
            mSharedPref.edit().putLong(LOCATION_FASTEST_UPDATE_INTERVAL, locationFastestUpdateInterval / 1000).commit();
            mSharedPref.edit().putLong(LOCATION_UPDATE_MAX_WAIT, locationUpdateMaxWait / 1000).commit();

        } catch (SecurityException e) {
            //            Utils.setRequestingLocationUpdates(this, false);
            e.printStackTrace();
        }
    }

    /**
     * Handles the Remove Updates button, and requests removal of location updates.
     */
    public void removeLocationUpdates(View view) {
        Log.i(TAG, "Removing location updates");
//        Utils.setRequestingLocationUpdates(this, false);
        mFusedLocationClient.removeLocationUpdates(getPendingIntent());
        mRequestUpdatesButton.setEnabled(true);
        mRemoveUpdatesButton.setEnabled(false);

        mSharedPref.edit().putBoolean(RUNNING, false);
    }

    /**
     * Handles the View Location Log button.
     */
    public void viewLocationLog(View view) {
        Intent intent = new Intent(this, LocationLogActivity.class);
        startActivity(intent);
    }

    /**
     * Handles the View Battery Log button.
     */
    public void viewBatteryLog(View view) {
        Intent intent = new Intent(this, BatteryLogActivity.class);
        startActivity(intent);
    }
}
