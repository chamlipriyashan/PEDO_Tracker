package com.workspike.pedotracker.pedotrackerv1;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.workspike.pedotracker.pedotrackerv1.my_clases.MainActivity;
import com.workspike.pedotracker.pedotrackerv1.not_used.DeviceControlActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Test2Activity extends AppCompatActivity {

    Button sync_button;
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    String bt_device_name=" ";
    String bt_device_id=" ";
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
    private TextView mConnectionState;
    private  TextView tv_console;

    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";




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
        tv_console.setText(R.string.no_data);
    }


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




   Test2Activity.HeartGraphView v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        tv_console = (TextView) findViewById(R.id.tv_console);
        mConnectionState = (TextView) findViewById(R.id.connection_state);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.canvas);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.FILL_PARENT);
         v = new Test2Activity.HeartGraphView(this);
        v.setLayoutParams(lp);
        relativeLayout.addView(v);

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

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    int globle_i =1;
    int[] mydataX = new int[1001];
    int[] mydataY = new int[1001];
    String previousString1="";
    String previousString2="";

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


            tv_console.setText(previousString2+""+previousString1+""+data);
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

        }else{
            System.out.println("connect device");
        }

        globle_i++;
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
