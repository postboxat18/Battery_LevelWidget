package com.examples.batterylevel.wbutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.examples.batterylevel.BatteryWidget;
import com.examples.batterylevel.R;

public class ButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);
        Button stopServiceButton = (Button) findViewById(R.id.stop_foreground_service_button);
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonActivity.this, BatteryWidget.UpdateService.class);
                intent.setAction(BatteryWidget.UpdateService.ACTION_STOP_FOREGROUND_SERVICE);
                startService(intent);
            }
        });
    }
}