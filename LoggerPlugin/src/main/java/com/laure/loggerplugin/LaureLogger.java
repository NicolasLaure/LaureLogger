package com.laure.loggerplugin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LaureLogger {
    private static final LaureLogger instance = new LaureLogger();
    private static final String LOGTAG = "Log -> ";
    private String fileName = "Laure_Log.txt";
    private static Activity unityActivity;

    AlertDialog.Builder builder;

    public static LaureLogger getInstance(){return instance;}
    private String allLogs = "";

    private LaureLogger()
    {
        Log.i(LOGTAG, "log manager started");
        this.allLogs = "Started plugin";
    }

    public void Initialize(Activity context)
    {
        unityActivity = context;
        unityActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
    }
    public void SendLog(String msj)
    {
        Log.i(LOGTAG, "SEND LOG: " + msj);
        this.allLogs += "\n" + LOGTAG + msj;
    }

    public String GetAllLogs()
    {
        Log.i(LOGTAG, "GET ALL: " + this.allLogs);
        return this.allLogs;
    }

    public void WriteToFile()
    {
        File path = unityActivity.getExternalFilesDir(null);
        try
        {
            DeleteLogFile();
            FileOutputStream writer = new FileOutputStream(new File(path, fileName));
            writer.write(this.allLogs.getBytes());
            writer.close();
            SendLog("Write Succeded at" + path.getPath());
            Toast.makeText(unityActivity.getApplicationContext(), "Wrote To File: " + fileName, Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            SendLog("Write Failed");
            Toast.makeText(unityActivity.getApplicationContext(), "Failed to Write To File: " + fileName, Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }

    public String ReadFromFile()
    {
        File path = unityActivity.getExternalFilesDir(null);
        String text = "";
        try
        {
            FileInputStream inputStream = new FileInputStream(new File(path, fileName));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String dataLine ="";
            while((dataLine = reader.readLine())!= null) {
                text += dataLine;
                text += '\n';
            }

            reader.close();
            SendLog("Read Succeded From" + path.getPath());
            Toast.makeText(unityActivity.getApplicationContext(), "Read From File: " + fileName, Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            SendLog("Failed To Read");
            Toast.makeText(unityActivity.getApplicationContext(), "Failed to Read From File: " + fileName, Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        return text;
    }

    public void ShowAlert() {
        AlertDialog alert = builder.create();
        builder.show();
    }

    public void DeleteLogs()
    {
        builder = new AlertDialog.Builder(unityActivity);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete the log file?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteLogFile();
                allLogs = "";
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void DeleteLogFile()
    {
        File log = new File(unityActivity.getApplicationContext().getExternalFilesDir(null), fileName);
        if(log.exists())
        {
            if (log.delete())
            {
                Toast.makeText(unityActivity.getApplicationContext(), "File " + fileName + " Deleted successfully.", Toast.LENGTH_SHORT).show();
            } else
            {
                Toast.makeText(unityActivity.getApplicationContext(), "Failed to delete " + fileName + " file.", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            SendLog("Couldn't find File to delete");
        }
    }
}