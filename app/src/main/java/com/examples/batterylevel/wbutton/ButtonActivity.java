package com.examples.batterylevel.wbutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.examples.batterylevel.BatteryWidget;
import com.examples.batterylevel.R;

import me.itangqi.waveloadingview.WaveLoadingView;

public class ButtonActivity extends AppCompatActivity {
    int i;
    boolean isCharging, usbCharge, acCharge;
    WaveLoadingView mWaveLoadingView;
    TextView txtbattery_status, txtstatus, txtcharge_status, txtport;
    private int progressStatus = 0;
    private String TAG="BatteryLevel";
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);
        Button stopServiceButton = (Button) findViewById(R.id.stop_foreground_service_button);
        Button startServiceButton = (Button) findViewById(R.id.start_foreground_service_button);
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonActivity.this, BatteryWidget.UpdateService.class);
                intent.setAction(BatteryWidget.UpdateService.ACTION_START_FOREGROUND_SERVICE);
                startService(intent);
            }
        });
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonActivity.this, BatteryWidget.UpdateService.class);
                intent.setAction(BatteryWidget.UpdateService.ACTION_STOP_FOREGROUND_SERVICE);
                startService(intent);
            }
        });

        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        txtbattery_status= (TextView) findViewById(R.id.txtbattery_status);
        txtstatus= (TextView) findViewById(R.id.txtstatus);
        txtcharge_status= (TextView) findViewById(R.id.txtcharge_status);
        txtport= (TextView) findViewById(R.id.txtport);
        BroadcastReceiver broadcastReceiverBattrery = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Integer integerBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
                i= Integer.parseInt(integerBatteryLevel.toString());
                Log.e("Battery","BatteryLevel"+i);
                if(i>=50 && i<=75){
                    txtstatus.setText("Battery Status: Moderate");
                }
                else if (i>=75 && i<=99){
                    txtstatus.setText("Battery Status: GOOD");
                }
                else if (i>=20 && i<=49){
                    txtstatus.setText("Battery Status: Draining");
                }
                else if (i>=10 && i<=20){
                    txtstatus.setText("Battery Status: LOW");
                }
                else if (i<10){
                    txtstatus.setText("Battery Status: Critically Low, Please Charge the Device!");
                }
                else if (i==100){

                    txtstatus.setText("Battery Status: Fully Charged");
                }
                mWaveLoadingView.setTopTitle(i+"%");
                mWaveLoadingView.setCenterTitleColor(Color.WHITE);
                mWaveLoadingView.setCenterTitle("");
                mWaveLoadingView.setBottomTitleSize(18);
                mWaveLoadingView.setProgressValue(0);
                mWaveLoadingView.setBorderWidth(5);
                mWaveLoadingView.setAmplitudeRatio(100);
                mWaveLoadingView.setWaveColor(Color.parseColor("#B2DFDB"));
                mWaveLoadingView.setBorderColor(Color.parseColor("#4CAF50"));
                mWaveLoadingView.setTopTitleStrokeColor(Color.WHITE);
                mWaveLoadingView.setTopTitleStrokeWidth(3);
                mWaveLoadingView.setWaterLevelRatio(0.2f);
                mWaveLoadingView.setAnimDuration(3000);
                mWaveLoadingView.pauseAnimation();
                mWaveLoadingView.resumeAnimation();
                mWaveLoadingView.cancelAnimation();
                mWaveLoadingView.startAnimation();
                new Thread(new Runnable() {
                    public void run() {
                        while (progressStatus < i) {
                            progressStatus += 2;
                            // Update the progress bar and display the
                            //current value in the text view
                            handler.post(new Runnable() {
                                public void run() {
                                    mWaveLoadingView.setProgressValue(progressStatus);
                                    String x=String.valueOf(i);
                                    txtbattery_status.setText("Battery Level:"+x+"%");
                                    getChargingLevel(getApplicationContext());

                                    //Battery charging conditions
                                    if (isCharging==true && usbCharge==true){
                                        txtcharge_status.setText("Charging Status: Device Charging");
                                        txtport.setText("Connected to a USB Port");
                                    }
                                    else if(isCharging==true && acCharge==true){
                                        txtcharge_status.setText("Charging Status: Device Charging");
                                        txtport.setText("Connected to an AC power source");
                                    }
                                    else
                                    {
                                        txtcharge_status.setText("Device Not Charging");
                                        txtport.setText( "Not Connected To Any Other Device");
                                    }
                                    if (isCharging==true && i==100){
                                        txtcharge_status.setText("Charging Status: Charging Complete");
                                    }


                                }
                            });
                            try {
                                // Sleep for 200 milliseconds.
                                //Just to display the progress slowly
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

                String x=String.valueOf(i);
                txtbattery_status.setText("Battery Level:"+x+"%");
                getChargingLevel(getApplicationContext());

                //Battery charging conditions
                if (isCharging==true && usbCharge==true){
                    txtcharge_status.setText("Charging Status: Device Charging");
                    txtport.setText("Connected to a USB Port");
                }
                else if(isCharging==true && acCharge==true){
                    txtcharge_status.setText("Charging Status: Device Charging");
                    txtport.setText("Connected to an AC power source");
                }
                else
                {
                    txtcharge_status.setText("Device Not Charging");
                    txtport.setText( "Not Connected To Any Other Device");
                }
                if (isCharging==true && i==100){
                    txtcharge_status.setText("Charging Status: Charging Complete");
                }

            }

        };
        this.registerReceiver(broadcastReceiverBattrery, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //i=getBatteryLevel(getApplicationContext());
        //battery level conditions
      /*  if(i>=50 && i<=75){
            txtstatus.setText("Battery Status: Moderate");
        }
        else if (i>=75 && i<=99){
            txtstatus.setText("Battery Status: GOOD");
        }
        else if (i>=20 && i<=49){
            txtstatus.setText("Battery Status: Draining");
        }
        else if (i>=10 && i<=20){
            txtstatus.setText("Battery Status: LOW");
        }
        else if (i<10){
            txtstatus.setText("Battery Status: Critically Low, Please Charge the Device!");
        }
        else if (i==100){

            txtstatus.setText("Battery Status: Fully Charged");
        }
        mWaveLoadingView.setTopTitle(i+"%");
        mWaveLoadingView.setCenterTitleColor(Color.WHITE);
        mWaveLoadingView.setCenterTitle("");
        mWaveLoadingView.setBottomTitleSize(18);
        mWaveLoadingView.setProgressValue(0);
        mWaveLoadingView.setBorderWidth(5);
        mWaveLoadingView.setAmplitudeRatio(100);
        mWaveLoadingView.setWaveColor(Color.parseColor("#B2DFDB"));
        mWaveLoadingView.setBorderColor(Color.parseColor("#4CAF50"));
        mWaveLoadingView.setTopTitleStrokeColor(Color.WHITE);
        mWaveLoadingView.setTopTitleStrokeWidth(3);
        mWaveLoadingView.setWaterLevelRatio(0.2f);
        mWaveLoadingView.setAnimDuration(3000);
        mWaveLoadingView.pauseAnimation();
        mWaveLoadingView.resumeAnimation();
        mWaveLoadingView.cancelAnimation();
        mWaveLoadingView.startAnimation();
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < i) {
                    progressStatus += 2;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            mWaveLoadingView.setProgressValue(progressStatus);
                            String x=String.valueOf(i);
                            txtbattery_status.setText("Battery Level:"+x+"%");
                            getChargingLevel(getApplicationContext());

                            //Battery charging conditions
                            if (isCharging==true && usbCharge==true){
                                txtcharge_status.setText("Charging Status: Device Charging");
                                txtport.setText("Connected to a USB Port");
                            }
                            else if(isCharging==true && acCharge==true){
                                txtcharge_status.setText("Charging Status: Device Charging");
                                txtport.setText("Connected to an AC power source");
                            }
                            else
                            {
                                txtcharge_status.setText("Device Not Charging");
                                txtport.setText( "Not Connected To Any Other Device");
                            }
                            if (isCharging==true && i==100){
                                txtcharge_status.setText("Charging Status: Charging Complete");
                            }


                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        //Just to display the progress slowly
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        String x=String.valueOf(i);
        txtbattery_status.setText("Battery Level:"+x+"%");
        getChargingLevel(getApplicationContext());

        //Battery charging conditions
        if (isCharging==true && usbCharge==true){
            txtcharge_status.setText("Charging Status: Device Charging");
            txtport.setText("Connected to a USB Port");
        }
        else if(isCharging==true && acCharge==true){
            txtcharge_status.setText("Charging Status: Device Charging");
            txtport.setText("Connected to an AC power source");
        }
        else
        {
            txtcharge_status.setText("Device Not Charging");
            txtport.setText( "Not Connected To Any Other Device");
        }
        if (isCharging==true && i==100){
            txtcharge_status.setText("Charging Status: Charging Complete");
        }

*/

    }
    //Checking the battery level.

    public int getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        Log.e(TAG,"Battery"+batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
        return i= batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

    }

    //Checking the charging status.
    public void getChargingLevel(Context bl){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = bl.registerReceiver(null, ifilter);
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

// How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
    }



}