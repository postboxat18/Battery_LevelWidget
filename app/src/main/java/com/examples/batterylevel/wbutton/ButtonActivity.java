package com.examples.batterylevel.wbutton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.examples.batterylevel.BatteryWidget;
import com.examples.batterylevel.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import me.itangqi.waveloadingview.WaveLoadingView;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class ButtonActivity extends AppCompatActivity {
    int i;
    boolean isCharging, usbCharge, acCharge;
    WaveLoadingView mWaveLoadingView,txtbattery_status;
    TextView  txtstatus, txtcharge_status, txtport,volTextView,text_level;
    private int progressStatus = 0;
    private String TAG="BatteryLevel";
    CustomGauge progress_bar;
    int batteryVol;
    float fullVoltage;
    String currentBatteryVol="Current Battery Voltage :";
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_prof);
        AppCompatButton stopServiceButton = (AppCompatButton) findViewById(R.id.stop_foreground_service_button);
        AppCompatButton startServiceButton = (AppCompatButton) findViewById(R.id.start_foreground_service_button);

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
        txtbattery_status = (WaveLoadingView) findViewById(R.id.txtbattery_status);
        progress_bar = findViewById(R.id.progress_bar);
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        txtbattery_status.setShapeType(WaveLoadingView.ShapeType.RECTANGLE);
        txtstatus= (TextView) findViewById(R.id.txtstatus);
        text_level= (TextView) findViewById(R.id.text_level);
        volTextView= (TextView) findViewById(R.id.volTextView);
        txtcharge_status= (TextView) findViewById(R.id.txtcharge_status);
        txtport= (TextView) findViewById(R.id.txtport);
        BroadcastReceiver broadcastReceiverBattrery = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Integer integerBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
                i= Integer.parseInt(integerBatteryLevel.toString());

                batteryVol = (int)(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0));
                fullVoltage = (float) (batteryVol * 0.001);
                volTextView.setText(fullVoltage+" volt");
                //Log.e("Battery","BatteryLevel"+i);
              /*  if(i>=50 && i<=75){
                    txtstatus.setText("Battery Status: Moderate");
                    mWaveLoadingView.setWaveColor(Color.parseColor("#B2DFDB"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#4CAF50"));
                }
                else if (i>=75 && i<=99){
                    txtstatus.setText("Battery Status: GOOD");
                    mWaveLoadingView.setWaveColor(Color.parseColor("#FF0AC511"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#FF37FF00"));
                }
                else if (i>=20 && i<=49){
                    txtstatus.setText("Battery Status: Draining");
                    mWaveLoadingView.setWaveColor(Color.parseColor("#FF6600"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#c0392b"));
                }
                else if (i>=10 && i<=20){
                    txtstatus.setText("Battery Status: LOW");
                    mWaveLoadingView.setWaveColor(Color.parseColor("#c0392b"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#E42434"));
                }
                else if (i<10){
                    txtstatus.setText("Battery Status: Critically Low, Please Charge the Device!");
                    mWaveLoadingView.setWaveColor(Color.parseColor("#E42434"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#FF0000"));
                }
                else if (i==100){
                    txtstatus.setText("Battery Status: Fully Charged");
                    mWaveLoadingView.setWaveColor(Color.parseColor("#FF26FF00"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#FF26FF00"));
                } */

                if (i<10){
                    txtstatus.setText("Critically Low, Please Charge the Device!");
                    mWaveLoadingView.setWaveColor(Color.parseColor("#FC0404"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#D40C1C"));

                    txtbattery_status.setWaveColor(Color.parseColor("#FC0404"));
                    txtbattery_status.setBorderColor(Color.parseColor("#D40C1C"));
                }
                else if (i>=10 && i<=20){
                    txtstatus.setText("LOW");
                    mWaveLoadingView.setWaveColor(Color.parseColor("#FC0412"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#FC0404"));
                    txtbattery_status.setWaveColor(Color.parseColor("#FC0412"));
                    txtbattery_status.setBorderColor(Color.parseColor("#FC0404"));

                }
                else if (i>20 && i<=50){
                    txtstatus.setText("Draining");
                    mWaveLoadingView.setWaveColor(Color.parseColor("#FCCC04"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#F4BB13"));
                    txtbattery_status.setWaveColor(Color.parseColor("#FCCC04"));
                    txtbattery_status.setBorderColor(Color.parseColor("#F4BB13"));

                }else if (i>50 && i<=80){
                    txtstatus.setText("Moderate");
                    txtbattery_status.setWaveColor(Color.parseColor("#94EC94"));
                    txtbattery_status.setBorderColor(Color.parseColor("#4BBC54"));
                    mWaveLoadingView.setWaveColor(Color.parseColor("#94EC94"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#4BBC54"));

                }
                else if (i>80 && i<=100){
                    txtstatus.setText("GOOD");
                    mWaveLoadingView.setWaveColor(Color.parseColor("#04E474"));
                    mWaveLoadingView.setBorderColor(Color.parseColor("#04E474"));
                    txtbattery_status.setWaveColor(Color.parseColor("#04E474"));
                    txtbattery_status.setBorderColor(Color.parseColor("#04E474"));
                }


                mWaveLoadingView.setTopTitle(i+"%");
                mWaveLoadingView.setCenterTitleColor(Color.WHITE);
                mWaveLoadingView.setCenterTitle("");
                mWaveLoadingView.setBottomTitleSize(18);
                mWaveLoadingView.setProgressValue(0);
                mWaveLoadingView.setBorderWidth(5);
                mWaveLoadingView.setAmplitudeRatio(100);
//                mWaveLoadingView.setWaveColor(Color.parseColor("#B2DFDB"));
//                mWaveLoadingView.setBorderColor(Color.parseColor("#4CAF50"));
                mWaveLoadingView.setTopTitleStrokeColor(Color.WHITE);
                mWaveLoadingView.setTopTitleStrokeWidth(3);
                mWaveLoadingView.setWaterLevelRatio(0.2f);
                mWaveLoadingView.setAnimDuration(3000);
                mWaveLoadingView.pauseAnimation();
                mWaveLoadingView.resumeAnimation();
                mWaveLoadingView.cancelAnimation();
                mWaveLoadingView.startAnimation();

                txtbattery_status.setTopTitle(i+"%");
                txtbattery_status.setCenterTitleColor(Color.WHITE);
                txtbattery_status.setCenterTitle("");
                txtbattery_status.setBottomTitleSize(18);
                txtbattery_status.setProgressValue(0);
                txtbattery_status.setBorderWidth(5);
                txtbattery_status.setAmplitudeRatio(100);
//                txtbattery_status.setWaveColor(Color.parseColor("#B2DFDB"));
//                txtbattery_status.setBorderColor(Color.parseColor("#4CAF50"));
                txtbattery_status.setTopTitleStrokeColor(Color.WHITE);
                txtbattery_status.setTopTitleStrokeWidth(3);
                txtbattery_status.setWaterLevelRatio(0.2f);
                txtbattery_status.setAnimDuration(3000);
                txtbattery_status.pauseAnimation();
                txtbattery_status.resumeAnimation();
                txtbattery_status.cancelAnimation();
                txtbattery_status.startAnimation();

//                progress_bar.setProgress(i);

                progress_bar.setValue(i);
                if(i<16) {
                    progress_bar.setPointStartColor(Color.parseColor("#E42434"));
                    progress_bar.setPointEndColor(Color.parseColor("#FC0404"));
                }
                else if(i>15 && i<31)
                {
                    progress_bar.setPointStartColor(Color.parseColor("#FCEC04"));
                    progress_bar.setPointEndColor(Color.parseColor("#FCFB20"));
                }
                text_level.setText(String.valueOf(i)+"%");


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
                                    txtbattery_status.setProgressValue(progressStatus);
                                    getChargingLevel(getApplicationContext());

                                    //Battery charging conditions
                                    if (isCharging==true && usbCharge==true){
                                        txtcharge_status.setText("Device Charging");
                                        txtport.setText("Connected to a USB Port");
                                    }
                                    else if(isCharging==true && acCharge==true){
                                        txtcharge_status.setText("Device Charging");
                                        txtport.setText("Connected to an AC power source");
                                    }
                                    else
                                    {
                                        txtcharge_status.setText("Not Charging");
                                        txtport.setText( "Not Connected To Any Other Device");
                                    }
                                    if (isCharging==true && i==100){
                                        txtcharge_status.setText("Charging Complete");
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


                getChargingLevel(getApplicationContext());

                //Battery charging conditions
                if (isCharging==true && usbCharge==true){
                    txtcharge_status.setText("Device Charging");
                    txtport.setText("Connected to a USB Port");
                }
                else if(isCharging==true && acCharge==true){
                    txtcharge_status.setText("Device Charging");
                    txtport.setText("Connected to an AC power source");
                }
                else
                {
                    txtcharge_status.setText("Not Charging");
                    txtport.setText( "Not Connected To Any Other Device");
                }
                if (isCharging==true && i==100){
                    txtcharge_status.setText("Charging Complete");
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