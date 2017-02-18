package com.example.sergeygirin.pisalka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

public class AutoStart extends BroadcastReceiver {





    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GIRIN_app_say", "AutoStart");
        MainActivity.beginSave();

        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(intent1);
    }



}
