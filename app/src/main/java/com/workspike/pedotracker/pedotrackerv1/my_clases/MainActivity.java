
package com.workspike.pedotracker.pedotrackerv1.my_clases;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.workspike.pedotracker.pedotrackerv1.BluetoothLeService;
import com.workspike.pedotracker.pedotrackerv1.DrawDynamicPathActivity;
import com.workspike.pedotracker.pedotrackerv1.draw_traking_path_test.DrawVectorActivity;
import com.workspike.pedotracker.pedotrackerv1.draw_traking_path_test.ManualMapDraw;
import com.workspike.pedotracker.pedotrackerv1.Test2Activity;
import com.workspike.pedotracker.pedotrackerv1.not_used.DeviceControlActivity;
import com.workspike.pedotracker.pedotrackerv1.DeviceScanActivity;
import com.workspike.pedotracker.pedotrackerv1.R;
import com.workspike.pedotracker.pedotrackerv1.SampleGattAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button sync_button;
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    String bt_device_name=" ";
    String bt_device_id=" ";
    static String fulltext="testttttttttttttttttttttttttttttttttttttstststststststststtsstst";
   TextView myTextView;
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
        tv_console.setText(R.string.no_data);
    }








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);g
        setContentView(R.layout.activity_main);
        TextView status = (TextView) findViewById(R.id.connected_status);
        tv_console = (TextView) findViewById(R.id.tv_console);
        myTextView = (TextView) findViewById(R.id.tv_pconsole);
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

            case R.id.action_manualdraw:
                final Intent intent = new Intent(this, ManualMapDraw.class);
                startActivity(intent);
                return true;


            case R.id.action_vector_draw:
                final Intent intent2 = new Intent(this, DrawVectorActivity.class);
                startActivity(intent2);
                return true;

            case R.id.action_test1:
                final Intent intent3 = new Intent(this, Test2Activity.class);
                startActivity(intent3);
                return true;

            case R.id.action_test2:
               final Intent intent4 = new Intent(this, DrawDynamicPathActivity.class);
                startActivity(intent4);
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

    private void displayData(String data) {

        if (data != null) {
           // tv_console.setText(data);




            if(fulltext.length()>=500){
                int index = fulltext.length()/2;
                fulltext.substring(0, index);
                fulltext+= "\n" + data;

                myTextView.setText(fulltext);
            }else{
                fulltext += "\n" + data;

                myTextView.setText(fulltext);
            }
            consolescroll.fullScroll(View.FOCUS_DOWN);

           // System.out.println(data);//**********************************************************************************
        }
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
                makeChange();
            }
        });
    }
    // on change of bars write char
    private void makeChange() {
        String str = SendValue + "\n";
        Log.d(TAG, "Sending result=" + SendValue);
        //Log.d(TAG, "Sending result=" + str);
        final byte[] tx = str.getBytes();
        System.out.println(SendValue);
        if(mConnected) {
            characteristicTX.setValue(String.valueOf(str));
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
            byte[] val;


           // val=characteristicRX.getValue();
           // String s=new String(val.toString());
           // System.out.println(s);



        }
    }







}
