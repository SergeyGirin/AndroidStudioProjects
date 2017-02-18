package com.example.user.car_blu;

        import android.annotation.TargetApi;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothGatt;
        import android.bluetooth.BluetoothGattCallback;
        import android.bluetooth.BluetoothGattCharacteristic;
        import android.bluetooth.BluetoothGattService;
        import android.bluetooth.BluetoothManager;
        import android.bluetooth.BluetoothProfile;
        import android.bluetooth.BluetoothSocket;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.ActivityInfo;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Point;
        import android.os.Build;
        import android.os.Environment;
        import android.os.PowerManager;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Display;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.SubMenu;
        import android.view.View;
        import android.view.MotionEvent;
        import android.content.Context;
        import android.graphics.Canvas;
        import android.widget.Toast;

        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.OutputStream;
        import java.math.BigDecimal;
        import java.math.RoundingMode;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;
        import java.util.UUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends ActionBarActivity {

    private static String GROVE_SERVICE =     "0000ffe0-0000-1000-8000-00805f9b34fb";
    private static String CHARACTERISTIC_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private static String CHARACTERISTIC_RX = "0000ffe1-0000-1000-8000-00805f9b34fb";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 2000; //2 seconds
    //private static final String DEVICE_NAME = "HMSoft"; //display name for Grove BLE
    private static final String DEVICE_NAME = "BT05"; //display name for Grove BLE

    private BluetoothAdapter mBluetoothAdapter;//our local adapter
    private BluetoothGatt mBluetoothGatt; //provides the GATT functionality for communication
    private BluetoothGattService mBluetoothGattService; //service on mBlueoothGatt
    private static List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();//discovered devices in range
    private BluetoothDevice mDevice; //external BLE device (Grove BLE module)
    private Timer mTimer;
    private Timer myTimer;



    // глобальные переменные
    // признак подключентя
    int isConnected = 0;
    // координаты центра экрана
    int isCentrX = 0;
    int isCentrY = 0;
    int isCentrR = 0;
    // для отработки касания
    float touchX =  0;
    float touchY =  0;
    int speedY = 0;
    int speedX = 0;
    // управления моторчиками (колёсами)
    int mLeft  = 0;
    int mRigth = 0;
    // протокол передачи команд
    // M - движенин;левое колесо;правое колесо;
    String message_old = "M;0;0;";
    String message = "C;0;0;";
    int isStart = 1;
    // запись в файл
    FileWriter myFileWriter;
    int isFileWriter = 0;
    long myTime;
    long myTimeOld;
    Menu menu;
    Menu myMenu;
    // чтение файлов
    File sdDir = Environment.getExternalStorageDirectory();
    File fileDir = new File(sdDir, "Chariot");


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GIRIN", "Begin");
        //setContentView(R.layout.activity_main);

        // фиксация ориентации
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        // отключение спящего режима
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        wl.acquire();




        mTimer = new Timer();
        //check to see if Bluetooth Low Energy is supported on this device
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Включаем bluetooth. Если он уже включен, то ничего не произойдет
        String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
        startActivityForResult(new Intent(enableBT), 0);

        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "BLE not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //Open settings if Bluetooth isn't enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth disabled", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //try to find the Grove BLE V1 module
        searchForDevices();
        Log.d("GIRIN", "графический режим");
        // графический режим
        // отображаем его в Activity
        setContentView(new GraphicsView(this));


        // размер экрана
        Display display = getWindowManager().getDefaultDisplay();
        Point isSize = new Point();
        display.getSize(isSize);
        isCentrX = isSize.x/2;
        isCentrY = isSize.y/2;
        isCentrR = isCentrX-isCentrX/5;
        touchX =  isCentrX;
        touchY =  isCentrY;
    }

    // Меню приложения
    // http://habrahabr.ru/post/222295/
    @Override
    public boolean onCreateOptionsMenu(Menu myMenu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, myMenu);
        // Установим видимость
        MenuItem miStop_save = myMenu.findItem(R.id.action_stop_save);
        miStop_save.setVisible(isFileWriter==1);
        MenuItem miSave = myMenu.findItem(R.id.action_save);
        miSave.setVisible(isFileWriter==0);
        menu = myMenu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
             case R.id.action_demo:
                 myDemo();
                 return true;

            case R.id.action_save:
                 beginSave();
                 return true;

            case R.id.action_stop_save:
                 stopSave();
                 return true;

            case R.id.action_read:
                 myRead();
                 return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void myDemo(){

        try {
            sendMessage("M;-150;100;");
            Thread.sleep(3000);
            sendMessage("M;0;0;");
            Thread.sleep(1000);
            sendMessage("M;100;-150;");
            Thread.sleep(3000);
            sendMessage("M;0;0;");
            Thread.sleep(100);
            sendMessage("M;100;100;");
            Thread.sleep(1500);
            sendMessage("M;0;0;");
            Thread.sleep(1000);
            sendMessage("M;-150;-150;");
            Thread.sleep(1500);
            sendMessage("M;0;0;");
            Thread.sleep(100);
            sendMessage("M;0;-150;");
            Thread.sleep(3000);
            sendMessage("M;-150;0;");
            Thread.sleep(3000);
            sendMessage("M;0;0;");
            Thread.sleep(1000);
            sendMessage("M;150;100;");
            Thread.sleep(3000);
            sendMessage("M;0;0;");
            Thread.sleep(1000);
            sendMessage("M;100;150;");
            Thread.sleep(3000);
            sendMessage("M;0;0;");
            Thread.sleep(100);
       } catch (InterruptedException e) {
            Log.d("GIRIN", "DemoError");
            e.printStackTrace();
        }

    }

    void beginSave(){
        sdDir = Environment.getExternalStorageDirectory();
        Calendar myCalendar = Calendar.getInstance();
        // добавляем свой каталог к пути

        if (isFileWriter==0){
            myTimeOld = System.currentTimeMillis();
            String myLog = String.format("%04d%02d%02d%02d%02d%02d"
                            , myCalendar.get(Calendar.YEAR)
                            , myCalendar.get(Calendar.MONTH)+1
                            , myCalendar.get(Calendar.DAY_OF_MONTH)
                            , myCalendar.get(Calendar.HOUR_OF_DAY)
                            , myCalendar.get(Calendar.MINUTE)
                            , myCalendar.get(Calendar.SECOND));



            String sdState = android.os.Environment.getExternalStorageState();
            if (!sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
                fileDir = getCacheDir();
            }
            if (!fileDir.exists())
                fileDir.mkdirs();
            try {
                myFileWriter = new FileWriter(fileDir+"/"+myLog+".txt");
                isFileWriter = 1;
             } catch (Exception e) {
                Log.d("GIRIN", e.getMessage());
            }
        }
        MenuItem miStop_save = menu.findItem(R.id.action_stop_save);
        miStop_save.setVisible(isFileWriter==1);
        MenuItem miSave = menu.findItem(R.id.action_save);
        miSave.setVisible(isFileWriter==0);
    }

    void myWriter(String TextLine){
        try {
            myTime = System.currentTimeMillis();
            Log.d("GIRIN ", " Time value in millisecinds "+(myTime-myTimeOld));
            myFileWriter.write("S;" + (myTime - myTimeOld) + ";" + TextLine + "\n");
            myFileWriter.flush();
            myTimeOld = myTime;
        } catch (Exception e) {
            Log.d("GIRIN", e.getMessage());
        }

    }

    void stopSave(){
        try {
            myFileWriter.flush();
            myFileWriter.close();
            isFileWriter = 0;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("GIRIN", e.getMessage());
        }
        MenuItem miStop_save = menu.findItem(R.id.action_stop_save);
        miStop_save.setVisible(isFileWriter==1);
        MenuItem miSave = menu.findItem(R.id.action_save);
        miSave.setVisible(isFileWriter==0);
    }
    void myRead(){
        SubMenu sm = menu.addSubMenu (Menu.FIRST, 401, 401, R.string.action_read);
        ArrayList<File> files = listFilesWithSubFolders(fileDir);
        for (int i=0;i<files.size();i++){
            Log.d("GIRIN", "" + files.get(i).getName());
            //myMenu.add (1, i, i, files.get(i).getName());
            sm.add(Menu.FIRST, 402+i, i, ""+files.get(i).getName());
        }


    }

   ArrayList<File> listFilesWithSubFolders(File dir) {
        ArrayList<File> files = new ArrayList<File>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                files.addAll(listFilesWithSubFolders(file));
            else
                files.add(file);
            //Log.d("GIRIN", ""+file.getName());
        }
        return files;
    }

    // Обработка кнопки "назад или выход"
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage("Выйти?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myExit();
                            android.os.Process.killProcess(android.os.Process.myPid());

                            finish();
                            return;
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    })
                    .show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    // процедура выхода
    void myExit()  {
        Log.d("GIRIN", "Exit");
        sendMessage("E;0;0;");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.d("GIRIN", "ExitError");
            e.printStackTrace();
        }
        if (mBluetoothGattService == null)
        {
            Log.d("GIRIN", "notBluetoothGattService");
        }
        else
        {
            Log.d("GIRIN", "closeBluetoothGattService");
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }


        /*
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        //Включаем bluetooth. Если он уже включен, то ничего не произойдет
        //bluetooth.disable();
    }
    // диалог на всякий случай
    boolean myBluetoothEnable() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("Включить?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .show();
        return true;

    }
    /*
    // отправка команды в поток блютуза
    void SendMessage (String myMessage){
        if (!myMessage.equals(message_old)) {
            //Получаем выходной поток для передачи данных
            Log.d("GIRIN", "Send ("+  myMessage + ");");
            if (isFileWriter == 1) myWriter(myMessage);
            try {
                if (isConnected == 1) {
                    OutputStream outStream = clientSocket.getOutputStream();
                    byte[] msgBuffer = myMessage.getBytes();
                    outStream.write(msgBuffer);
                    outStream.write((int) 10);
                    message_old = myMessage;
                }
            } catch (IOException e){
                e.printStackTrace();
                Log.d("GIRIN", "OutputStream: " + e.getMessage());
                isConnected = 0 ;
            }
            message_old = myMessage;
        }

    }
    */

    public class GraphicsView extends View {
        Paint mPaint = new Paint();
        public GraphicsView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Рисуем желтый круг
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.YELLOW);
            canvas.drawCircle(isCentrX, isCentrY, isCentrR, mPaint);

            if (isConnected == 1) {
                mPaint.setColor(Color.BLUE);
            } else {
                mPaint.setColor(Color.RED);
            }

            mPaint.setTextSize(30);
            canvas.drawText("" +(new BigDecimal(touchX).setScale(1, RoundingMode.HALF_UP).floatValue()), 10, 50, mPaint);
            canvas.drawText("" +(new BigDecimal(touchY).setScale(1, RoundingMode.HALF_UP).floatValue()), 10, 100, mPaint);
            // ограничение точа
            if (touchX<isCentrX-isCentrR){touchX=isCentrX-isCentrR;}
            if (touchX>isCentrX+isCentrR){touchX=isCentrX+isCentrR;}
            if (touchY<isCentrY-isCentrR){touchY=isCentrY-isCentrR;}
            if (touchY>isCentrY+isCentrR){touchY=isCentrY+isCentrR;}

            // преобразование в 255 единиц
            speedX = (int) (((float) 255)/ (float) isCentrR*( touchX - (float)isCentrX));
            speedY = (int) (((float) 255)/ (float) isCentrR*((float) isCentrY - touchY));
            // рисую шарик с органичением шарика внутри круга
            int mLeng = (int) Math.sqrt(Math.pow(Math.abs(speedX),2)+Math.pow(Math.abs(speedY),2));
            float mUgol =0;
            double rUgol = 0;
            double x1 = 0;
            double y1 = 0;
            if (mLeng>0) {
                if (speedY<0){
                    mUgol = (float)180 -(float)speedX/(float)mLeng*(float)90;
                } else {
                    mUgol =  (float) speedX / (float) mLeng * (float) 90;
                }
                rUgol = mUgol*Math.PI/(double) 180;
                double x = isCentrX;
                double y = isCentrY;
                double x0 = isCentrX;
                double y0 = isCentrY-isCentrR;
                double alpha = rUgol;


                double rx = x0 - x;
                double ry = y0 - y;
                double c = Math.cos(alpha);
                double s = Math.sin(alpha);
                x1 = x + rx * c - ry * s;
                y1 = y + rx * s + ry * c;
            }
            if (mLeng>isCentrR){
                canvas.drawCircle((float)x1, (float)y1, isCentrR/10, mPaint);
                mPaint.setStrokeWidth(isCentrR/54);
                canvas.drawLine (isCentrX, isCentrY,(float)x1, (float)y1, mPaint);
            } else {
                canvas.drawCircle(touchX, touchY, isCentrR/10, mPaint);
                mPaint.setStrokeWidth(isCentrR/54);
                canvas.drawLine (isCentrX, isCentrY,touchX, touchY, mPaint);
            }

            canvas.drawText( ""+speedX,300, 50, mPaint);
            canvas.drawText( ""+speedY,400, 50, mPaint);
            // управление двигателями
            if (speedY>0) {
                if (speedX < 0) {
                    mLeft =  speedY + speedX;
                    mRigth = speedY ;
                } else {
                    mLeft = speedY;
                    mRigth = speedY - speedX;
                }
            } else {
                if (speedX < 0) {
                    mLeft = speedY - speedX;
                    mRigth = speedY;
                } else {
                    mLeft = speedY;
                    mRigth = speedY + speedX;
                }
            }
            // инверсия двигателей
            if (mLeft<0) mLeft = -256 - mLeft;
            if (mRigth<0) mRigth = -256 - mRigth;


            canvas.drawText( ""+mLeft,300, 100, mPaint);
            canvas.drawText( ""+mRigth,400, 100, mPaint);
            if (isStart == 1){
                isStart = 0;
            } else {
                message = "M;"+mLeft+";"+mRigth+";";
            }
            sendMessage(message);
     }




        // обработка нажатий точпада
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ) {
                touchX = event.getX();
                touchY = event.getY();
                invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                touchX = isCentrX;
                touchY = isCentrY;
                invalidate();
            }
            return true;
        }
    }
    private void searchForDevices ()
    {
        statusUpdate("Searching for devices ...");

        if(mTimer != null) {
            mTimer.cancel();
        }

        scanLeDevice();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                statusUpdate("Search complete");
                findGroveBLE();
            }
        }, SCAN_PERIOD);
    }
    private void scanLeDevice() {
        new Thread() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);

                try {
                    Thread.sleep(SCAN_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }.start();
    }
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            if (device != null) {
                if (mDevices.indexOf(device) == -1)//to avoid duplicate entries
                {
                    if (DEVICE_NAME.equals(device.getName())) {
                        mDevice = device;//we found our device!
                    }
                    mDevices.add(device);
                }
            }
        }
    };
    private void findGroveBLE ()
    {
        if(mDevices == null || mDevices.size() == 0)
        {
            statusUpdate("No BLE devices found");
            return;
        }
        else if(mDevice == null)
        {
            statusUpdate("Unable to find Grove BLE");
            return;
        }
        else
        {
            statusUpdate("Found Grove BLE V1");
            statusUpdate("Address: " + mDevice.getAddress());
            connectDevice();
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean connectDevice ()
    {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mDevice.getAddress());
        if (device == null) {
            statusUpdate("Unable to connect");
            return false;
        }
        // directly connect to the device
        statusUpdate("Connecting ...");
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        return true;
    }
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                statusUpdate("Connected");
                statusUpdate("Searching for services");
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                statusUpdate("Device disconnected");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();

                for(BluetoothGattService gattService : gattServices) {
                    statusUpdate("Service discovered: " + gattService.getUuid());
                    if(GROVE_SERVICE.equals(gattService.getUuid().toString()))
                    {
                        mBluetoothGattService = gattService;
                        statusUpdate("Found communication Service");
                        isConnected = 1;



                        sendMessage("C;0;0;");
                        //sendMessage("M;45;45;");
                        myTimer = new Timer();
                        myTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendMessage("M;45;45;");
                            }
                        }, 1000);
                        myTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendMessage("M;0;0;");
                            }
                        }, 1200);
                        myTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendMessage("M;45;45;");
                            }
                        }, 1300);
                        myTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendMessage("M;0;0;");
                            }
                        }, 1500);
                        myTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendMessage("M;45;45;");
                            }
                        }, 1600);
                        myTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendMessage("M;0;0;");
                            }
                        }, 1800);

                    }
                }
            } else {
                statusUpdate("onServicesDiscovered received: " + status);
            }
        }
    };

    private void sendMessage (String myMessage)
    {
        if (mBluetoothGattService == null)
            return;

        //statusUpdate("Finding Characteristic...");
        BluetoothGattCharacteristic gattCharacteristic =
                mBluetoothGattService.getCharacteristic(UUID.fromString(CHARACTERISTIC_TX));

        if(gattCharacteristic == null) {
            statusUpdate("Couldn't find TX characteristic: " + CHARACTERISTIC_TX);
            return;
        }

        //statusUpdate("Found TX characteristic: " + CHARACTERISTIC_TX);

        String msg = myMessage+"\n";
        if (!msg.equals(message_old)) {
            statusUpdate("Sending message:"+" '"+myMessage+"'");
            byte[] temp = msg.getBytes();
            gattCharacteristic.setValue(temp);
            mBluetoothGatt.writeCharacteristic(gattCharacteristic);
            message_old = msg;
        }
    }

    private void statusUpdate (final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("GIRIN", msg);
            }
        });
    }





}

