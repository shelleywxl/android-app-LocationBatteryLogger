package com.example.android.locationbatterylogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class LocationLogActivity extends AppCompatActivity {

    //UI
    private Button mBackToMain;
    private Button mClearLogButton;
    private WebView mLogWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_log);

        mBackToMain = (Button) findViewById(R.id.button_backtomain);
        mClearLogButton = (Button) findViewById(R.id.button_clearlog);
        mLogWebView = (WebView) findViewById(R.id.webview_location_log);

        WebSettings wSettings = mLogWebView.getSettings();
        wSettings.setBuiltInZoomControls(true);
        wSettings.setSupportZoom(true);
        wSettings.setDefaultFontSize(14);
        mLogWebView.loadDataWithBaseURL(null, LocationLog.getInstance(this).readFromFile(), "text/plain", "UTF-8", null);
    }

    /**
     * Handles the Back button.
     */
    public void backToMain(View view) {
        Intent intent = new Intent(LocationLogActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Handles the Clear Log button.
     */
    public void clearLocationLog(View view) {
        LocationLog.getInstance(this).clearLogFile();
        LocationLog.clearInstance();
        mLogWebView.loadDataWithBaseURL(null, LocationLog.getInstance(this).readFromFile(), "text/plain", "UTF-8", null);
    }
}
