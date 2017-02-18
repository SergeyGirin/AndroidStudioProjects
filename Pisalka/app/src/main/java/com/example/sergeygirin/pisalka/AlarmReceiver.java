package com.example.sergeygirin.pisalka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GIRIN_app_say", "AlarmReceiver");
        //PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        //wl.acquire();

        //Log.d("GIRIN_app_say", "intent: "+intent.toString());
        //Log.d("GIRIN_app_say", "intent.getAction: "+intent.getAction().toString() );
        //Log.d("GIRIN_app_say", "intent.getAction: " );


        //MainActivity.beginSave();
        //try {
        //    MainActivity.mySaveMessage("AlarmReceiver");
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        //Intent intent1 = new Intent(context, MainActivity.class);
        //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(intent1);
    }

}