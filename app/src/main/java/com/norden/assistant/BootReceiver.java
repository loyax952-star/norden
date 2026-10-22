package com.norden.assistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                && NordenService.isEnabled(context)) {
            ContextCompat.startForegroundService(context, new Intent(context, NordenService.class));
        }
    }
}
