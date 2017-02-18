package com.example.sergeygirin.pisalka;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("GIRIN_app_say", "Приложение запустилось");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beginSave();
        Timer timer = new Timer();
        timer.schedule(new UpdateTimeTask(), 0, 1 * 60 * 1000); //тикаем  без задержки

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class UpdateTimeTask extends TimerTask {
        public void run() {
            //задача для таймера
            int myLac = 0;
            int myCellID = 0 ;
            try {
                Log.d("GIRIN_app_say", "-=*TimerTask*=- " );
                mySaveMessage("Timer");
            } catch (IOException e) {
                Log.d("GIRIN_app_say", "-Error-=*TimerTask*=- " + e.getMessage());
                e.printStackTrace();
            }
            /*
            TelephonyManager TelephonyMgr = null;
            if (GlobalVar.getPhoneManagerLocation() == null) {
                TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                GlobalVar.setPhoneManagerLocation(TelephonyMgr);
            } else {
                TelephonyMgr=GlobalVar.getPhoneManagerLocation();
            }
            CellLocation cl = TelephonyMgr.getCellLocation();

            if (cl instanceof GsmCellLocation) {
                GsmCellLocation gcl = (GsmCellLocation) cl;
                 myLac = gcl.getLac();
                 myCellID = gcl.getCid();
            }

            String myLocation = GlobalVar.getPhoneManagerLocation().getCellLocation().toString();
            Log.d("GIRIN_app_say", "-=*TimerTask*=- " + myLocation);
            Log.d("GIRIN_app_say", "-=*getCellLocation*=- " + myLac +";"+myCellID);

            try {
                mySaveMessage(myLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
        }
    }

    public static void mySaveMessage(String myMessage) throws IOException {
        FileWriter myFileWriter = new FileWriter(GlobalVar.getLogFile(),true);
        Calendar myCalendar = Calendar.getInstance();
        String myLog = String.format("%04d%02d%02d%02d%02d%02d"
                , myCalendar.get(Calendar.YEAR)
                , myCalendar.get(Calendar.MONTH) + 1
                , myCalendar.get(Calendar.DAY_OF_MONTH)
                , myCalendar.get(Calendar.HOUR_OF_DAY)
                , myCalendar.get(Calendar.MINUTE)
                , myCalendar.get(Calendar.SECOND));

        myFileWriter.write(myLog+";"+myMessage+"\n");
        myFileWriter.flush();
        myFileWriter.close();
        //Log.d("GIRIN_app_say", "-=* Записано *=- " + myMessage);
    }

    public static  void beginSave(){
        if (GlobalVar.getLogFile() == null) {
            GlobalVar.setDir(new File(Environment.getExternalStorageDirectory(), "Pisalka"));
        }
        if (!GlobalVar.getDir().exists()) {
            GlobalVar.getDir().mkdirs();
            Log.d("GIRIN_app_say", "Каталог создан");
        }

        if (GlobalVar.getLogFile() == null) {
            Calendar myCalendar = Calendar.getInstance();
            String myLog = String.format("%04d%02d%02d%02d%02d%02d"
                    , myCalendar.get(Calendar.YEAR)
                    , myCalendar.get(Calendar.MONTH) + 1
                    , myCalendar.get(Calendar.DAY_OF_MONTH)
                    , myCalendar.get(Calendar.HOUR_OF_DAY)
                    , myCalendar.get(Calendar.MINUTE)
                    , myCalendar.get(Calendar.SECOND));

            String sdState = android.os.Environment.getExternalStorageState();
            if (!sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
                //fileDir = getCacheDir();
            }
            try {
                FileWriter myFileWriter = new FileWriter(GlobalVar.setLogFile(GlobalVar.getDir() + "/" + myLog + ".txt"));
                Log.d("GIRIN_app_say", "Файл создан");
                //myFileWriter.write(myMessage+"\n");
                myFileWriter.flush();
                myFileWriter.close();
            } catch (Exception e) {
                Log.d("GIRIN_app_say", e.getMessage());
            }
        }
    }
}

//public class MainActivity2 extends ActionBarActivity {


