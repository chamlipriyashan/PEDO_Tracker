
package com.workspike.pedotracker.pedotrackerv1.my_clases;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.workspike.pedotracker.pedotrackerv1.BluetoothLeService;
import com.workspike.pedotracker.pedotrackerv1.draw_traking_path_test.DrawVectorActivity;
import com.workspike.pedotracker.pedotrackerv1.Test2Activity;
import com.workspike.pedotracker.pedotrackerv1.not_used.DeviceControlActivity;
import com.workspike.pedotracker.pedotrackerv1.DeviceScanActivity;
import com.workspike.pedotracker.pedotrackerv1.R;
import com.workspike.pedotracker.pedotrackerv1.SampleGattAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    int globle_i =1;
    int[] mydataX = new int[1001];
    int[] mydataY = new int[1001];


    Button sync_button;
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    String bt_device_name=" ";
    String bt_device_id=" ";
    static String fulltext="testttttttttttttttttttttttttttttttttttttstststststststststtsstst";
   TextView console_tv;
    ScrollView consolescroll;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String mDeviceName;
    private String mDeviceAddress;
    private SeekBar mRed;
    int SendValue=0;
    private int[] RGBFrame = {0,0,0};
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
   // private TextView mConnectionState;
    private  TextView tv_console;

    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    final Handler handler = new Handler();
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void clearUI() {
        console_tv.setText(R.string.no_data);
    }





    MainActivity.HeartGraphView v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView status = (TextView) findViewById(R.id.connected_status);
        tv_console = (TextView) findViewById(R.id.tv_console);
        console_tv = (TextView) findViewById(R.id.tv_pconsole);
        consolescroll =(ScrollView) findViewById(R.id.consolescroll);
       // mConnectionState = (TextView) findViewById(R.id.connection_state);
        this.setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button sync_button=(Button)findViewById(R.id.sync_button);
        sync_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), DeviceScanActivity.class);
               startActivity(i);
            }
        });


        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.canvas2);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.FILL_PARENT);
         v = new MainActivity.HeartGraphView(this);
        v.setLayoutParams(lp);
        relativeLayout.addView(v);


        Intent intent = getIntent();
        bt_device_name= intent.getStringExtra(EXTRAS_DEVICE_NAME);
        bt_device_id = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        System.out.println(bt_device_name+"   "+bt_device_id);
        status.setText(bt_device_name +" / "+bt_device_id);

        final Intent intent2 = getIntent();
        mDeviceName = intent2.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent2.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mRed = (SeekBar) findViewById(R.id.seekRed);
        readSeek(mRed,0);
       // readSeek(mRed,1);
       // readSeek(mRed,2);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);




    }






    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        mConnected=false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;




            case R.id.action_vector_draw:
                final Intent intent2 = new Intent(this, DrawVectorActivity.class);
                startActivity(intent2);
                return true;

            case R.id.action_test1:
                final Intent intent3 = new Intent(this, Test2Activity.class);
                startActivity(intent3);
                return true;


            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              //  mConnectionState.setText(resourceId);
                System.out.println(resourceId);
            }
        });
    }

    String previousString1="";
    String previousString2="";
//    String previousString3="";
//    String previousString4="";
//    String previousString5="";
//    String previousString6="";
//    String previousString7="";
//    String previousString8="";

int angle=0;

    private void displayData(String data) {

        if (data != null) {



                List<String> items = Arrays.asList(data.split(","));
              //  String[] animalsArray = data.split("\\s*,\\s*");
              //  String   aa= (items.get(1));
              //  String bb= (items.get(2));
              //  System.out.println(animalsArray[0] +"  ---  "+animalsArray[1]+"     " + animalsArray[2]  );


            int length_ofpath= Integer.parseInt(items.get(0));
            int angle360= Integer.parseInt(items.get(1));

                System.out.println(length_ofpath);
                System.out.println(angle360);


                mydataX[ globle_i] = (int) (2*length_ofpath*Math.cos(angle360));
                mydataY[ globle_i] = (int) (2*length_ofpath*Math.sin(angle360));

            v.invalidate();


            console_tv.setText(previousString2+""+previousString1+""+data);
//  console_tv.setText(previousString8+"\n"+previousString7+"\n"+previousString6+"\n"+previousString5+"\n"+previousString4+"\n"+previousString3+"\n"+previousString2+"\n"+previousString1+"\n"+data);
//            previousString8=previousString7;
//            previousString7=previousString6;
//            previousString6=previousString5;
//
//            previousString5=previousString4;
//            previousString4=previousString3;
//            previousString3=previousString2;
            previousString2=previousString1;
            previousString1=data;

        }

        globle_i++;
    }


    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();


        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

            // If the service exists for HM 10 Serial, say so.

            if(SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {
                System.out.println("Yes, serial :-)");
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                currentServiceData.put(LIST_UUID, uuid);
                gattServiceData.add(currentServiceData);

                // get characteristic when UUID matches RX/TX UUID
                characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
                characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);



                makeChange(2);
            } else {
                System.out.println("No, serial :-(");
            }
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // get characteristic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void readSeek(SeekBar seekBar,final int pos) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                RGBFrame[pos]=progress;
                SendValue=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                makeChange(1);
            }
        });
    }
    // on change of bars write char
    private void makeChange(int x) {
        String str = SendValue + "\n";
        Log.d(TAG, "Sending result=" + SendValue);
        //Log.d(TAG, "Sending result=" + str);
        final byte[] tx = str.getBytes();
        System.out.println(SendValue);
        if(mConnected) {
            if(characteristicTX==null){
                System.out.println("Disconnect the device and re connect man!  or RETRY");
            }else {
                characteristicTX.setValue(String.valueOf(str));
                mBluetoothLeService.writeCharacteristic(characteristicTX);
                mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                byte[] val;//this is used to send value to the device


                if(x==2){
                    System.out.println("teeeeeee");
                    characteristicTX.setValue(String.valueOf("test"));
                    mBluetoothLeService.writeCharacteristic(characteristicTX);
                    mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                }
            }
        }
    }



    public class HeartGraphView extends View {
        public HeartGraphView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            Init_floor_map();
        }


        Bitmap board;
        int i=0;
        Paint paint = new Paint();
        int randomX=0;
        int randomY=0;

        int min = 100;
        int max = 480;
        public Paint mPaint;
        public Canvas mCanvas;
        int startX;
        int startY;
        int endX;
        int endY;
        private PointF startPoint, endPoint;

        private void Init_floor_map() {

            board= BitmapFactory.decodeResource(getResources(),R.drawable.floor1map);
            paint.setColor(Color.BLUE);
        }


        int[] dataX = new int[30];
        int[] dataY = new int[30];

        public int generatRandomPositiveNegitiveValue(int max, int min) {
            Random r = new Random();
            int ii = r.nextInt(max - min + 1) + min;
            return (ii - 140);
        }


        public void getlocation() {
            randomX=generatRandomPositiveNegitiveValue(max, min);
            randomY=generatRandomPositiveNegitiveValue(max, min);

          //  System.out.println("get location");
           // return (ii - 140);

        }


        Point start_point;
        Rect frameToDraw;
        RectF whereToDraw;
        int Y0 ;
        int X0;


        @Override
        public void onDraw(Canvas canvas) {
            int w;
            int h;
            h = 280;
            w = 600;
           Y0 = canvas.getHeight();
           X0 = canvas.getWidth()/2;
           start_point= new Point(X0, Y0);
             frameToDraw = new Rect(0, 0, board.getWidth(),board.getHeight());
             whereToDraw = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(board,frameToDraw,whereToDraw, paint);
            int min = 0;
            int max = 280;

            paint.setStrokeWidth(6);



 //************************************************Graph plotin********************perfect*************************************
     /*

            dataX[0] = 0;
            for (int i = 0; i < w / 20 - 1; i++) {
                dataX[i + 1] = (i + 1) * w / 20;
                getlocation();
                dataY[w / 20 - 1] = generatRandomPositiveNegitiveValue(max, min);
                dataY[i] = dataY[i + 1];
            }

            for (int i = 0; i < w / 20 - 1; i++) {
                // apply some transformation on data in order to map it correctly
                // in the coordinates of the canvas
                canvas.drawLine(dataX[i], h / 2 - dataY[i], dataX[i + 1], h / 2 - dataY[i + 1], paint);
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.MULTIPLY);
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
                invalidate();
            }
*/
//************************************************Graph plotin********************perfect*************************************



            canvas.drawLine(start_point.x,start_point.y, (canvas.getWidth()/3), 100, paint);//start,end

            mydataX[0] = start_point.x;
            mydataY[0] = start_point.y;

            canvas.drawLine(mydataX[0],mydataY[0],mydataX[1], mydataY[1], paint);
//            for (int i = 0; i < X0 ; i++) {
//                dataX[i + 1] = (i + 1) * X0 / 20;
//                //dataX[i]=getlocation(10,100);
//                dataY[X0 / 20 - 1] = generatRandomPositiveNegitiveValue(max, min);
//                dataY[i] = dataY[i + 1];
//            }

            for (int i = 1; i < globle_i-1; i++) {
                canvas.drawLine(X0+mydataX[i], Y0+mydataY[i],X0+mydataX[i+1], Y0+mydataY[i+1], paint);
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                //try {
             //       Thread.sleep(10);
              //  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
              //      e.printStackTrace();
             //   }
             // invalidate();
            }


















        }
    }








}
