package com.example.sergeygirin.pisalka;

import android.media.MediaRecorder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey.Girin on 01.10.2015.
 */
public class GlobalVar {
    private static MediaRecorder callrecorderPivate;
    private static TelephonyManager phoneManagerPrivate;
    private static TelephonyManager phoneManagerLocation;
    private static Map<MediaRecorder, TelephonyManager> myRecorders = new HashMap<MediaRecorder, TelephonyManager>();
    private static boolean isLog = false;
    private static File myDir;
    private static String myLog;

    public static MediaRecorder getCallrecorder() {
        return GlobalVar.callrecorderPivate;
    }

    public static void setCallrecorder(MediaRecorder myCallrecorderPivate) {
        GlobalVar.callrecorderPivate = myCallrecorderPivate;
        if ( myCallrecorderPivate != null) {
            myRecorders.put(myCallrecorderPivate, GlobalVar.phoneManagerPrivate);
        }
        if (isLog) Log.d("GIRIN_app_say", "== myRecorders.size == : " + myRecorders.size());

    }

    public static TelephonyManager getPhoneManager() {
        return GlobalVar.phoneManagerPrivate;
    }

    public static void setPhoneManager(TelephonyManager myPhoneManagerPrivate) {
        GlobalVar.phoneManagerPrivate = myPhoneManagerPrivate;
    }

    public static TelephonyManager getPhoneManagerFind(MediaRecorder myCallrecorderPivate) {
        if (isLog) Log.d("GIRIN_app_say", "-= getPhoneManagerFind =-" );
        if (isLog) Log.d("GIRIN_app_say", "myCallrecorderPivate : " + myCallrecorderPivate.toString());
        TelephonyManager myResult = myRecorders.get(myCallrecorderPivate);
        if (isLog) Log.d("GIRIN_app_say", "myResult : " + myResult.toString());
        return myResult;
    }

    public static void PhoneManagerRemove(MediaRecorder myCallrecorderPivate) {
        if (isLog) Log.d("GIRIN_app_say", "== myRecorders.removeBegin.size == : "+myRecorders.size());
        if (isLog) Log.d("GIRIN_app_say", "-= PhoneManagerRemove =-" );
        if (isLog) Log.d("GIRIN_app_say", "myCallrecorderPivate : " + myCallrecorderPivate.toString());
        TelephonyManager myResult = myRecorders.remove(myCallrecorderPivate);
        if (myResult !=null){
            if (isLog) Log.d("GIRIN_app_say", "Удалил" );
            if (isLog) Log.d("GIRIN_app_say", "== myResult == : " + myResult.toString());
        } else {
            if (isLog) Log.d("GIRIN_app_say", "Не удалил" );
        }


        if ( myRecorders.isEmpty())  {
            if (isLog) Log.d("GIRIN_app_say", "== Пусто == : "+myRecorders.size());
            myRecorders.clear();
        } else {
            if (isLog) Log.d("GIRIN_app_say", "== Не пусто == : "+myRecorders.size());
        }

        if (isLog) Log.d("GIRIN_app_say", "== myRecorders.removeEnd.size == : "+myRecorders.size());
    }

    public static String getLogFile() {
        return GlobalVar.myLog;
    }
    public static String setLogFile(String myFileName) {
        GlobalVar.myLog = myFileName;
        return GlobalVar.myLog;
    }

    public static File getDir() {
        return GlobalVar.myDir;
    }
    public static File setDir(File myFileName) {
        GlobalVar.myDir = myFileName;
        return GlobalVar.myDir;
    }
    public static TelephonyManager getPhoneManagerLocation() {
        return GlobalVar.phoneManagerLocation;
    }

    public static void setPhoneManagerLocation(TelephonyManager myPhoneManager) {
        GlobalVar.phoneManagerLocation = myPhoneManager;
    }
}
