package com.example.myapplication.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.myapplication.services.MyService;

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionMusic = intent.getIntExtra("action_music", 0);

        Intent intentService = new Intent(context, MyService.class);
        intentService.putExtra("action_music_service", actionMusic);

        context.startService(intentService);
    }
}
