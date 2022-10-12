package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.services.MyService;

public class MediaPlayerActivity extends AppCompatActivity {

    RelativeLayout layoutMediaPlayer;

    ImageView imgDown, imgSong, imgFavorite;

    TextView txtNameSong, txtSingerSong, txtRunTime, txtTotalTime;

    SeekBar seekBar;

    ImageView imgShuffle, imgPrevious, imgPlayOrPause, imgNext, imgRepeat;

    MyService mService;
    boolean isServiceConnected;
    BroadcastReceiver mBroadcastReceiver;

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder myBinder = (MyService.MyBinder) iBinder;
            mService = myBinder.getMyService();
            isServiceConnected = true;

            handleMediaPlayer();

            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int action = intent.getIntExtra("action_music", 0);
                    handleActionMediaPlayer(action);
                }
            };

            LocalBroadcastManager.getInstance(MediaPlayerActivity.this).registerReceiver(mBroadcastReceiver, new IntentFilter("send_action_to_activity"));

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            isServiceConnected = false;
        }
    };



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        initView();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mService.mMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        imgFavorite.setOnClickListener(view -> {
            handleOnClickFavorite();
        });

//        layoutMediaPlayer.setOnTouchListener((view, motionEvent) -> {
//            float x = motionEvent.getX();
//            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
//                Toast.makeText(this, "onTouch", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//            return true;
//        });


        Intent intent = new Intent(MediaPlayerActivity.this, MyService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);


    }

    private void initView() {
        layoutMediaPlayer = findViewById(R.id.layout_media_player);
        imgDown = findViewById(R.id.image_down);
        imgSong = findViewById(R.id.image_song);
        imgFavorite = findViewById(R.id.image_favorite);
        txtNameSong = findViewById(R.id.text_name_song);
        txtSingerSong = findViewById(R.id.text_singer_song);
        txtRunTime = findViewById(R.id.text_run_time);
        txtTotalTime = findViewById(R.id.text_total_time);

        seekBar = findViewById(R.id.seek_bar);
        imgShuffle = findViewById(R.id.image_shuffle);
        imgPrevious = findViewById(R.id.image_previous);
        imgPlayOrPause = findViewById(R.id.image_play_or_pause);
        imgNext = findViewById(R.id.image_next);
        imgRepeat = findViewById(R.id.image_repeat);

        imgDown.setOnClickListener(view -> {
            finish();
            onBackPressed();
        });


    }

    private void handleMediaPlayer() {
        imgSong.setImageResource(mService.mSong.getImageResource());
        txtNameSong.setText(mService.mSong.getName());
        txtSingerSong.setText(mService.mSong.getSinger());

        setStatusPlayOrPause();

        imgPlayOrPause.setOnClickListener(view -> {
            if (mService.isPlaying) {
                mService.pauseMusic();
                stopAnimationImageSong();
                sendActionToService(MyService.ACTION_PAUSE);
            } else {
                mService.resumeMusic();
                sendActionToService(MyService.ACTION_RESUME);
            }

            setStatusPlayOrPause();
        });

        imgPrevious.setOnClickListener(view -> {
            //TODO
        });

        imgNext.setOnClickListener(view -> {
            //TODO
        });


    }

    private void handleActionMediaPlayer(int action) {
        switch (action) {
            case MyService.ACTION_START:
                handleMediaPlayer();
                setStatusPlayOrPause();
                break;

            case MyService.ACTION_PAUSE:
                setStatusPlayOrPause();
                break;

            case MyService.ACTION_RESUME:
                setStatusPlayOrPause();
                break;
        }
    }


    private void setStatusPlayOrPause() {
        if (mService == null) {
            return;
        }

        if (mService.isPlaying) {
            startAnimationImageSong();
            imgPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_circle_24);
        } else {
            imgPlayOrPause.setImageResource(R.drawable.ic_baseline_play_circle_24);
            stopAnimationImageSong();
        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music_service", action);
        startService(intent);
    }

    private void startAnimationImageSong() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imgSong.animate().rotationBy(360).withEndAction(this).setDuration(20000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };

        imgSong.animate().rotationBy(360).withEndAction(runnable).setDuration(20000)
                .setInterpolator(new LinearInterpolator()).start();

    }

    private void stopAnimationImageSong() {
        imgSong.animate().cancel();
    }

    private void handleOnClickFavorite() {

    }
}