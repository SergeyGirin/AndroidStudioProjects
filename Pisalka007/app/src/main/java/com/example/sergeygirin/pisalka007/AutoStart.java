package com.example.sergeygirin.pisalka007;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

public class AutoStart extends BroadcastReceiver {

    FileWriter myFileWriter;
    File sdDir = Environment.getExternalStorageDirectory();
    File fileDir = new File(sdDir, "Pisalka");
    int isFileWriter = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GIRIN_app_say", "Реакция на событие");
        beginSave();
        //Intent intent1 = new Intent(context, MainActivity.class);
        //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(intent1);
    }


    void beginSave(){
        Log.d("GIRIN_app_say", "Запись файла");
        sdDir = Environment.getExternalStorageDirectory();
        Calendar myCalendar = Calendar.getInstance();
        // добавляем свой каталог к пути

        if (isFileWriter==0){

            String myLog = String.format("%04d%02d%02d%02d%02d%02d"
                    , myCalendar.get(Calendar.YEAR)
                    , myCalendar.get(Calendar.MONTH)+1
                    , myCalendar.get(Calendar.DAY_OF_MONTH)
                    , myCalendar.get(Calendar.HOUR_OF_DAY)
                    , myCalendar.get(Calendar.MINUTE)
                    , myCalendar.get(Calendar.SECOND));



            String sdState = android.os.Environment.getExternalStorageState();
            if (!sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
                //fileDir = getCacheDir();
            }
            if (!fileDir.exists()) {
                fileDir.mkdirs();
                Log.d("GIRIN_app_say", "Каталог создан");
            }
            try {
                myFileWriter = new FileWriter(fileDir+"/"+myLog+".txt");
                Log.d("GIRIN_app_say", "Файл создан");
                isFileWriter = 1;
            } catch (Exception e) {
                Log.d("GIRIN_app_say", e.getMessage());
            }
            isFileWriter=1;

        }

    }

}
