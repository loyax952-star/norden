package com.norden.assistant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Switch;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    private static final int REQ_NOTIF = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch sw = findViewById(R.id.powerSwitch);
        sw.setChecked(NordenService.isEnabled(this));

        sw.setOnCheckedChangeListener((view, on) -> {
            if (on) {
                if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                        this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
                }
                NordenService.setEnabled(this, true);
                ContextCompat.startForegroundService(this, new Intent(this, NordenService.class));
            } else {
                NordenService.setEnabled(this, false);
                stopService(new Intent(this, NordenService.class));
            }
        });
    }
}
