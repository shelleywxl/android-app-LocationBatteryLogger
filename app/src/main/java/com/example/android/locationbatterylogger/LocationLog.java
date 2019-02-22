package com.example.android.locationbatterylogger;

import android.content.Context;
import android.location.Location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationLog {
    private static final String LOCATION_LOG_FILE = "location.log";
    private final String TAG = this.getClass().getSimpleName();

    private static LocationLog instance;
    private File locationLogFile;
    private FileWriter fileWriter;

    LocationLog(Context context) {
        this.locationLogFile = new File(context.getExternalFilesDir(null), LOCATION_LOG_FILE);
        try {
            fileWriter = new FileWriter(this.locationLogFile, true);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void clearLogFile() {
        try {
            fileWriter = new FileWriter(this.locationLogFile);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void logLocation(String lat, String lng) {
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String time = format.format(new Date());
        String message = lat + "  " + lng + "    " + time + "\n";
        try {
            fileWriter = new FileWriter(this.locationLogFile, true);
            fileWriter.write(message);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    public static LocationLog getInstance(Context context) {
        if (instance == null) {
            instance = new LocationLog(context);
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    public String readFromFile() {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(locationLogFile));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
        } catch (IOException io) { io.printStackTrace();}
        finally {
            try {
                reader.close();
            } catch(IOException io) {}
        }
        return fileData.toString();
    }
}
