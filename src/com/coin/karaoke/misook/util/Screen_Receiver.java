package com.coin.karaoke.misook.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Screen_Receiver extends BroadcastReceiver {

    public static boolean ScreenOn = true;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
        	ScreenOn = false;
        }else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
        	ScreenOn = true;
        }
    }
}
