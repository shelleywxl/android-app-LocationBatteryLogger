package com.example.android.locationbatterylogger;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BatteryLog {
    private static final String BATTERY_LOG_FILE = "battery.log";
    private final String TAG = this.getClass().getSimpleName();

    private static BatteryLog instance;
    private File batteryLogFile;
    private FileWriter fileWriter;

    BatteryLog(Context context) {
        this.batteryLogFile = new File(context.getExternalFilesDir(null), BATTERY_LOG_FILE);
        try {
            fileWriter = new FileWriter(this.batteryLogFile, true);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void clearLogFile() {
        try {
            fileWriter = new FileWriter(this.batteryLogFile);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void logBattery(String level) {
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String time = format.format(new Date());
        String message = "Battery Level: " + level + "    Time: " + time + "\n";
        try {
            fileWriter = new FileWriter(this.batteryLogFile, true);
            fileWriter.write(message);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    public static BatteryLog getInstance(Context context) {
        if (instance == null) {
            instance = new BatteryLog(context);
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
            reader = new BufferedReader(new FileReader(batteryLogFile));
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
