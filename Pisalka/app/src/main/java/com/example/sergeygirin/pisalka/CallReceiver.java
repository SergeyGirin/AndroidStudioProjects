package com.example.sergeygirin.pisalka;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;


public class CallReceiver extends BroadcastReceiver {
    String phoneNumber = "";

    int isFileWriter;
    Context contextc;
    //MediaRecorder callrecorder;

    public void onReceive(Context context, Intent intent) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        GlobalVar.setPhoneManager(TelephonyMgr);


        //AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //Intent i = new Intent(context, AlarmReceiver.class);
        //PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 1, pi); // Millisec * Second * Minute

        //callrecorder = GlobalVar.getCallrecorder() ;
        contextc=context;
        Log.d("GIRIN_app_say", "-=*=-" );
        Log.d("GIRIN_app_say", "TelephonyManager: "+TelephonyMgr.toString() );
        Log.d("GIRIN_app_say", "-=*getLine1Number()*=- "+TelephonyMgr.getLine1Number());
        Log.d("GIRIN_app_say", "-=*getNetworkOperator()*=- "+TelephonyMgr.getNetworkOperator());
        Log.d("GIRIN_app_say", "-=*getNetworkOperatorName()*=- "+TelephonyMgr.getNetworkOperatorName());
        Log.d("GIRIN_app_say", "-=*getSimOperator()*=- "+TelephonyMgr.getSimOperator());
        Log.d("GIRIN_app_say", "-=*getSimOperatorName()*=- "+TelephonyMgr.getSimOperatorName());

        Log.d("GIRIN_app_say", "intent: "+intent.toString() );
        Log.d("GIRIN_app_say", "intent: "+intent.getStringExtra(TelephonyManager.EXTRA_STATE) );


        Log.d("GIRIN_app_say", "intent.getAction: "+intent.getAction().toString() );
        Log.d("GIRIN_app_say", "intent.getAction.EXTRA_STATE: "+intent.getStringExtra(TelephonyManager.EXTRA_STATE) );

        //Toast.makeText(context, intent.getAction().toString(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, "N - "+intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER), Toast.LENGTH_SHORT).show();

        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            //получаем исходящий номер
            phoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            Log.d("GIRIN_app_say", "phoneNumber: "+phoneNumber );
            callrecorder_begin(phoneNumber);
        } else if (intent.getAction().equals("android.intent.action.PHONE_STATE")){
            String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d("GIRIN_app_say", "PHONE_STATE: "+phone_state);
            if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //телефон звонит, получаем входящий номер
                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d("GIRIN_app_say", "phoneNumber: "+phoneNumber );
                callrecorder_begin(phoneNumber);

            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                //телефон находится в режиме звонка (набор номера / разговор)
                //Vibrator v = (Vibrator)contextc.getSystemService(Context.VIBRATOR_SERVICE);
                //v.vibrate(3000);
            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Log.d("GIRIN_app_say", "EXTRA_STATE_IDLE:");
                //телефон находиться в ждущем режиме. Это событие наступает по окончанию разговора, когда мы уже знаем номер и факт звонка
                Log.d("GIRIN_app_say", "Нормальный выход");
                callrecorder_stop(GlobalVar.getCallrecorder());

                //Vibrator v = (Vibrator)contextc.getSystemService(Context.VIBRATOR_SERVICE);
                //v.vibrate(3000);
            }
        }
    }




    void callrecorder_begin(String phoneNumber) {

        MediaRecorder myCallrecorder = new MediaRecorder();
        Log.d("GIRIN_app_say", "Begin callrecorder");
        Log.d("GIRIN_app_say", "callrecorder: " +myCallrecorder.toString());
        GlobalVar.setCallrecorder(myCallrecorder) ;

        Log.d("GIRIN_app_say", "phoneNumber: "+phoneNumber );

        Calendar myCalendar = Calendar.getInstance();

        String myLog = String.format("%04d%02d%02d%02d%02d%02d"
                , myCalendar.get(Calendar.YEAR)
                , myCalendar.get(Calendar.MONTH)+1
                , myCalendar.get(Calendar.DAY_OF_MONTH)
                , myCalendar.get(Calendar.HOUR_OF_DAY)
                , myCalendar.get(Calendar.MINUTE)
                , myCalendar.get(Calendar.SECOND));

        myCallrecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        myCallrecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myCallrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        myCallrecorder.setOutputFile(GlobalVar.getDir() + "/" +myLog+'_'+ phoneNumber +".mp4");

        myCallrecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {

                //Log.d("GIRIN_app_say", "*****************");
                //Log.d("GIRIN_app_say", "MediaRecorder: " + mr.toString());
                //Log.d("GIRIN_app_say", "MediaRecorder what: " + what);
                //Log.d("GIRIN_app_say", "MediaRecorder extra: " + extra);
                //Log.d("GIRIN_app_say", "MediaRecorder CallState: " + GlobalVar.getPhoneManager().getCallState());
                //Log.d("GIRIN_app_say", "MediaRecorder CallState: " + GlobalVar.getPhoneManagerFind(mr).getCallState());

                if (GlobalVar.getPhoneManagerFind(mr).getCallState()==0){
                    Log.d("GIRIN_app_say", "******* Остановливаю запись **********");
                    callrecorder_stop(mr);
                }
             }
        });

        try {
            myCallrecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        myCallrecorder.start();
        Log.d("GIRIN_app_say", "******* Начало записи **********");
    }

    void callrecorder_stop(MediaRecorder myCallrecorder) {
        if (myCallrecorder != null) {
            Log.d("GIRIN_app_say", "Закрываю callrecorder");
            Log.d("GIRIN_app_say", "callrecorder: " + myCallrecorder.toString());
            GlobalVar.PhoneManagerRemove(myCallrecorder);
            myCallrecorder.stop();
            myCallrecorder.reset();
            myCallrecorder.release();
            myCallrecorder = null;
            GlobalVar.setCallrecorder(myCallrecorder);
        } else {
            Log.d("GIRIN_app_say", "Пустой callrecorder");

        }
    }







}