package com.norden.assistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())) {
            try {
                ContextCompat.startForegroundService(c, new Intent(c, NordenService.class));
            } catch (Exception e) {
                // कुछ फ़ोन boot पर mic service नहीं चलने देते; app एक बार खोल दें
            }
        }
    }
}
