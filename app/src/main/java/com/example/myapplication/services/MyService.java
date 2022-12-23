package com.example.myapplication.services;

import static com.example.myapplication.applications.MusicApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.activities.MediaPlayerActivity;
import com.example.myapplication.receivers.ActionReceiver;
import com.example.myapplication.models.Song;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {

    public MediaPlayer mMediaPlayer;
    public static boolean isPlaying;
    public static List<Song> mData = new ArrayList<>();
    public static Song mSong;

    public static final int ACTION_START = 0;
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_PREVIOUS = 3;
    public static final int ACTION_NEXT = 4;

    //public static int position = -1;

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

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

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
//        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notifyIntent.setAction(Intent.ACTION_MAIN);
        notifyIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP );
// Create the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), song.getImageResource());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setContentTitle(song.getName())
                .setContentText(song.getSinger())
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(false)
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

        Uri artworkUri = song.getAlbumUri();
        if (artworkUri != null) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            Bitmap bitmap = null;
            try {
                mmr.setDataSource(song.getUrl());
                byte[] artBytes = mmr.getEmbeddedPicture();
                if (artBytes != null) {
                    bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
                }
            } catch (Exception e) {
                Log.v("Song", "Song Doesn't Have Embedded Picture " + song.getUrl());
                e.printStackTrace();
            }
            mmr.release();
            builder.setLargeIcon(bitmap);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), song.getImageResource());
            builder.setLargeIcon(bitmap);
        }

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, ActionReceiver.class);
        intent.putExtra("action_music", action);

        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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

        sendMediaPlayerNotification(mSong);
        sendActionToActivity(ACTION_PAUSE);
    }

    public void resumeMusic() {
        if (mMediaPlayer != null && !isPlaying) {
            mMediaPlayer.start();
            isPlaying = true;
        }

        sendMediaPlayerNotification(mSong);
        sendActionToActivity(ACTION_RESUME);
    }

    public void previousSong() {
        int position = -1;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getUrl().equals(mSong.getUrl())) {
                position = i;
            }
        }

        if (MediaPlayerActivity.isShuffle) {
            int randomPosition = (int) (Math.random() * mData.size());
            while (randomPosition == position) {
                randomPosition = (int) (Math.random() * mData.size());
            }
            Log.v("position_random", randomPosition + "");
            position = randomPosition;
            mSong = mData.get(position);
        } else {
            Log.v("position", position + "");
            if (position - 1 >= 0) {
                position -= 1;
            } else {
                position = mData.size() - 1;
            }
            mSong = mData.get(position);
            Log.v("position", "mSong_position" + position);
        }
        changedSong(mSong);

        sendMediaPlayerNotification(mSong);
        sendActionToActivity(ACTION_PREVIOUS);
    }

    public void nextSong() {
        int position = -1;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getUrl().equals(mSong.getUrl())) {
                position = i;
            }
        }

        if (MediaPlayerActivity.isShuffle) {
            int randomPosition = (int) (Math.random() * mData.size());
            while (randomPosition == position) {
                randomPosition = (int) (Math.random() * mData.size());
            }
            Log.v("position_random", randomPosition + "");
            position = randomPosition;
            mSong = mData.get(position);
        } else {
            Log.v("position", position + "");
            if (position + 1 <= mData.size() - 1) {
                position += 1;
            } else {
                position = 0;
            }
            mSong = mData.get(position);
            Log.v("position", "mSong_position" + position);
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
                break;

            case ACTION_RESUME:
                resumeMusic();
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
