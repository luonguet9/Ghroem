package com.example.myapplication.services;

import static com.example.myapplication.applications.MusicApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.applications.MusicApplication;
import com.example.myapplication.receivers.ActionReceiver;
import com.example.myapplication.models.Song;

public class MyService extends Service {

    public MediaPlayer mMediaPlayer;
    public static boolean isPlaying;
    public static Song mSong;

    public static final int ACTION_START = 0;
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_PREVIOUS = 3;
    public static final int ACTION_NEXT = 4;

    IBinder myBinder = new MyBinder();

    public class MyBinder extends Binder {
        public MyService getMyService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.v("MyService", "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("MyService", "onStartCommand");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song song = (Song) bundle.get("object_song");
            if (song != null) {
                mSong = song;
                startMusic(mSong);
                sendMediaPlayerNotification(mSong);
            }
        }

        int action = intent.getIntExtra("action_music_service", 0);
        handleMediaPlayerNotification(action);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("MyService", "onBind");
        return myBinder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("MyService", "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.v("MyService", "onDestroy");

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

    private void sendMediaPlayerNotification(Song song) {
//        Intent resultIntent = new Intent(this, MainActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addNextIntentWithParentStack(resultIntent);
//
//        PendingIntent pendingIntent =
//                stackBuilder.getPendingIntent(0,
//                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent notifyIntent = new Intent(this, MainActivity.class);
// Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Create the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), song.getImageResource());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setContentTitle(song.getName())
                .setContentText(song.getSinger())
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2));

        if (isPlaying) {
            builder.addAction(R.drawable.ic_baseline_skip_previous_black_24, "Previous", getPendingIntent(this, ACTION_PREVIOUS))
                    .addAction(R.drawable.ic_baseline_pause_24, "Pause", getPendingIntent(this, ACTION_PAUSE))
                    .addAction(R.drawable.ic_baseline_skip_next_black_24, "Next", getPendingIntent(this, ACTION_NEXT));
        } else {
            builder.addAction(R.drawable.ic_baseline_skip_previous_black_24, "Previous", getPendingIntent(this, ACTION_PREVIOUS))
                    .addAction(R.drawable.ic_baseline_play_arrow_24, "Resume", getPendingIntent(this, ACTION_RESUME))
                    .addAction(R.drawable.ic_baseline_skip_next_black_24, "Next", getPendingIntent(this, ACTION_NEXT));
        }

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, ActionReceiver.class);
        intent.putExtra("action_music", action);

        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void startMusic(Song song) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(song.getUrl()));
        }
        mMediaPlayer.start();
        isPlaying = true;
        sendActionToActivity(ACTION_START);
    }

    public void pauseMusic() {
        if (mMediaPlayer != null && isPlaying) {
            mMediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void resumeMusic() {
        if (mMediaPlayer != null && !isPlaying) {
            mMediaPlayer.start();
            isPlaying = true;
        }
    }

    public void previousSong() {
        int position = MusicApplication.mData.indexOf(mSong);
        if (position - 1 >= 0) {
            mSong = MusicApplication.mData.get(position - 1);
            Log.v("mSong", "position - 1");
        } else {
            mSong = MusicApplication.mData.get(MusicApplication.mData.size() - 1);
            Log.v("mSong", "size - 1");
        }
        changedSong(mSong);

        sendMediaPlayerNotification(mSong);
        sendActionToActivity(ACTION_PREVIOUS);
    }

    public void nextSong() {
        int position = MusicApplication.mData.indexOf(mSong);
        if (position + 1 <= MusicApplication.mData.size() - 1) {
            mSong = MusicApplication.mData.get(position + 1);
            Log.v("mSong", "position + 1");
        } else {
            mSong = MusicApplication.mData.get(0);
            Log.v("mSong", "0");
        }
        changedSong(mSong);

        sendMediaPlayerNotification(mSong);
        sendActionToActivity(ACTION_NEXT);
    }

    public void changedSong(Song song) {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.release();
            }
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(song.getUrl()));
            mMediaPlayer.start();
            isPlaying = true;
        }
    }

    private void handleMediaPlayerNotification(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                sendMediaPlayerNotification(mSong);
                sendActionToActivity(action);
                break;

            case ACTION_RESUME:
                resumeMusic();
                sendMediaPlayerNotification(mSong);
                sendActionToActivity(action);
                break;

            case ACTION_PREVIOUS:
                previousSong();
                break;

            case ACTION_NEXT:
                nextSong();
                break;
        }
    }

    private void sendActionToActivity(int action) {
        Intent intent = new Intent("send_action_to_activity");

        intent.putExtra("action_music", action);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
