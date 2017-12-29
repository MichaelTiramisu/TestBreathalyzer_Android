package com.wangguan.testbreathalyzer;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import BACtrackAPI.API.BACtrackAPI;
import BACtrackAPI.API.BACtrackAPICallbacks;
import BACtrackAPI.Constants.BACTrackDeviceType;
import BACtrackAPI.Exceptions.BluetoothLENotSupportedException;
import BACtrackAPI.Exceptions.BluetoothNotEnabledException;
import BACtrackAPI.Exceptions.LocationServicesNotEnabledException;

public class MainActivity extends Activity {

    private static final byte PERMISSIONS_FOR_SCAN = 100;

    private static final String APIKEY = "8994f5202fea469a903cb731f64cfc";

    private BACtrackAPI mAPI;

    private Button mConnectBreathalyzerButton;
    private Button mStartBlowButton;
    private TextView mStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        try {
            mAPI = new BACtrackAPI(this, mCallbacks, APIKEY);
        } catch (BluetoothLENotSupportedException e) {
            e.printStackTrace();
        } catch (BluetoothNotEnabledException e) {
            e.printStackTrace();
        } catch (LocationServicesNotEnabledException e) {
            e.printStackTrace();
        }

    }

    private void initUI() {
        mConnectBreathalyzerButton = (Button) findViewById(R.id.connect_button);
        mConnectBreathalyzerButton.setOnClickListener(mConnectBreathalyzerButtonClickListener);

        mStartBlowButton = (Button) findViewById(R.id.start_blow_button);
        mStartBlowButton.setOnClickListener(mStartBlowButtonClickListener);

        mStateTextView = (TextView) findViewById(R.id.state_textView);
        mStateTextView.setText("Disconnected");
    }

    View.OnClickListener mConnectBreathalyzerButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mAPI != null) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_FOR_SCAN);
                } else {
                    mAPI.connectToNearestBreathalyzer();
                }
            }
        }
    };

    View.OnClickListener mStartBlowButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mAPI!= null) {
                mAPI.startCountdown();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_FOR_SCAN: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /**
                     * Only start scan if permissions granted.
                     */
                    mAPI.connectToNearestBreathalyzer();
                }
            }
        }
    }

    private void setStates(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStateTextView.setText(message);
            }
        });
    }

    private final BACtrackAPICallbacks mCallbacks = new BACtrackAPICallbacks() {
        @Override
        public void BACtrackAPIKeyDeclined(String s) {
            Log.d("DEBUG", s);
        }

        @Override
        public void BACtrackAPIKeyAuthorized() {
            Log.d("DEBUG", "很好！");
        }

        @Override
        public void BACtrackConnected(BACTrackDeviceType bacTrackDeviceType) {
            setStates("Connected!");
        }

        @Override
        public void BACtrackDidConnect(String s) {

        }

        @Override
        public void BACtrackDisconnected() {

        }

        @Override
        public void BACtrackConnectionTimeout() {
            Log.d("DEBUG", "BACtrackConnectionTimeout");
        }

        @Override
        public void BACtrackFoundBreathalyzer(BluetoothDevice bluetoothDevice) {

        }

        @Override
        public void BACtrackCountdown(int countdown) {
            setStates("Blow Starts in " + countdown + " seconds");
        }

        @Override
        public void BACtrackStart() {
            setStates("Start Blow");
        }

        @Override
        public void BACtrackBlow() {
            setStates("Keep Blow");
        }

        @Override
        public void BACtrackAnalyzing() {
            setStates("Analyzing...");
        }

        @Override
        public void BACtrackResults(float result) {
            setStates("Result: " + result);
        }

        @Override
        public void BACtrackFirmwareVersion(String s) {

        }

        @Override
        public void BACtrackSerial(String s) {

        }

        @Override
        public void BACtrackUseCount(int i) {

        }

        @Override
        public void BACtrackBatteryVoltage(float v) {

        }

        @Override
        public void BACtrackBatteryLevel(int i) {

        }

        @Override
        public void BACtrackError(int i) {

        }
    };

}
