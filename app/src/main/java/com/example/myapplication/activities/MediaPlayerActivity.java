package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.models.Song;
import com.example.myapplication.services.MyService;

import java.text.SimpleDateFormat;

public class MediaPlayerActivity extends AppCompatActivity {

    RelativeLayout layoutMediaPlayer;

    ImageView imgDown, imgSong, imgFavorite;

    TextView txtNameSong, txtSingerSong, txtRunTime, txtTotalTime;

    SeekBar seekBar;

    ImageView imgShuffle, imgPrevious, imgPlayOrPause, imgNext, imgRepeat;

    MyService mService;
    public boolean isServiceConnected;
    BroadcastReceiver mBroadcastReceiver;

    public static boolean isShuffle;

    public static final int REPEAT_ALL_ON = 2;
    public static final int REPEAT_ONE_ON = 1;
    public static final int REPEAT_OFF = 0;
    public static int statusRepeat = REPEAT_OFF;

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
            //finish();
            onBackPressed();
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isServiceConnected) {
            unbindService(mServiceConnection);
            isServiceConnected = false;
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        finish();
    }

    private void handleMediaPlayer() {
        //imgSong.setImageResource(MyService.mSong.getImageResource());
        Uri artworkUri = MyService.mSong.getAlbumUri();
        if (artworkUri != null) {
            imgSong.setImageURI(artworkUri);
        } else {
            imgSong.setImageResource(R.drawable.spotify_blue);
        }
        txtNameSong.setText(MyService.mSong.getName());
        txtSingerSong.setText(MyService.mSong.getSinger());

        setStatusPlayOrPause();
        setStatusShuffle();
        setStatusRepeat();
        setStatusFavorite();
        setTimeTotal();
        updateTime();

        imgPlayOrPause.setOnClickListener(view -> {
            if (MyService.isPlaying) {
                sendActionToService(MyService.ACTION_PAUSE);
            } else {
                sendActionToService(MyService.ACTION_RESUME);
            }

            setStatusPlayOrPause();
        });

        imgPrevious.setOnClickListener(view -> {
            sendActionToService(MyService.ACTION_PREVIOUS);
            handleMediaPlayer();
        });

        imgNext.setOnClickListener(view -> {
            sendActionToService(MyService.ACTION_NEXT);
            handleMediaPlayer();
        });

        imgShuffle.setOnClickListener(view -> {
            if (isShuffle) {
                isShuffle = false;
            } else {
                isShuffle = true;
            }

            setStatusShuffle();
        });

        imgRepeat.setOnClickListener(view -> {
            if (statusRepeat == REPEAT_OFF) {
                statusRepeat = REPEAT_ONE_ON;
            } else if (statusRepeat == REPEAT_ONE_ON) {
                statusRepeat = REPEAT_ALL_ON;
            } else {
                statusRepeat = REPEAT_OFF;
            }

            setStatusRepeat();
        });

        imgFavorite.setOnClickListener(view -> {
            if (MyService.mSong.isLove) {
                MyService.mSong.isLove = false;
                //Delete from favorite playlist
                deleteSongFromFavoritePlaylist(MyService.mSong);
            } else {
                MyService.mSong.isLove = true;
                // Add to favorite playlist
                addSongToFavoritePlaylist(MyService.mSong);
            }
            setStatusFavorite();
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

            case MyService.ACTION_PREVIOUS:
                handleMediaPlayer();
                break;

            case MyService.ACTION_NEXT:
                handleMediaPlayer();
                break;
        }
    }


    private void setStatusPlayOrPause() {
        if (mService == null) {
            return;
        }

        if (MyService.isPlaying) {
            startAnimationImageSong();
            imgPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_circle_24);
        } else {
            imgPlayOrPause.setImageResource(R.drawable.ic_baseline_play_circle_24);
            stopAnimationImageSong();
        }
    }

    private void setStatusShuffle() {
        if (isShuffle) {
            imgShuffle.setImageResource(R.drawable.ic_baseline_shuffle_on_24);
        } else {
            imgShuffle.setImageResource(R.drawable.ic_baseline_shuffle_off_24);
        }
    }

    private void setStatusRepeat() {
        if (statusRepeat == REPEAT_OFF) {
            imgRepeat.setImageResource(R.drawable.ic_baseline_repeat_off_24);
        } else if (statusRepeat == REPEAT_ONE_ON) {
            imgRepeat.setImageResource(R.drawable.ic_baseline_repeat_one_24);
        } else {
            imgRepeat.setImageResource(R.drawable.ic_baseline_repeat_on_24);
        }
    }

    private void setStatusFavorite() {
        for (Song song : AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(1)) {
            if (song.getUrl().equals(MyService.mSong.getUrl())) {
                MyService.mSong.isLove = true;
                break;
            }
        }

        if (MyService.mSong.isLove) {
            imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_on_24);
        } else {
            imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_off_24);
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



    private void setTimeTotal() {
        SimpleDateFormat time = new SimpleDateFormat("mm:ss");
        txtTotalTime.setText(time.format(mService.mMediaPlayer.getDuration()));
        seekBar.setMax(mService.mMediaPlayer.getDuration());
    }

    private void updateTime() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mService.mMediaPlayer != null && isServiceConnected) {
                    mService.mMediaPlayer.setOnCompletionListener(mediaPlayer -> onCompletionListener());
                    SimpleDateFormat time = new SimpleDateFormat("mm:ss");
                    txtRunTime.setText(time.format(mService.mMediaPlayer.getCurrentPosition()));
                    seekBar.setProgress(mService.mMediaPlayer.getCurrentPosition());
                }
                handler.postDelayed(this, 200);
            }
        }, 200);

    }

    private void onCompletionListener() {
        int position = -1;
        for (int i = 0; i < MyService.mData.size(); i++) {
            if (MyService.mData.get(i).getUrl().equals(MyService.mSong.getUrl())) {
                position = i;
            }
        }

        if (statusRepeat == REPEAT_OFF) {
            if (position + 1 <= MyService.mData.size() - 1 || isShuffle) {
                sendActionToService(MyService.ACTION_NEXT);
            } else {
                sendActionToService(MyService.ACTION_PAUSE);
            }
        } else if (statusRepeat == REPEAT_ONE_ON) {
            mService.changedSong(MyService.mSong);
        } else {
            // REPEAT ALL ON
            sendActionToService(MyService.ACTION_NEXT);
        }
        if (mService.mMediaPlayer != null) {
            setTimeTotal();
            handleMediaPlayer();
        }
    }

    private void addSongToFavoritePlaylist(Song song){
        for (Song temp : AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(1)) {
            if (temp.getUrl().equals(song.getUrl())) {
                Toast.makeText(this, "This song has exists!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        song.isLove = true;
        Song newSong = song.copySong();
        newSong.isLove = true;
        newSong.playlistId = 1;
        PlaylistDetailActivity.viewModel.insertSong(newSong);
        Toast.makeText(this, "Added to favorite playlist", Toast.LENGTH_SHORT).show();
    }

    private void deleteSongFromFavoritePlaylist(Song song) {
        song.isLove = false;
        for (Song temp : AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(1)) {
            if (temp.getUrl().equals(song.getUrl())) {
                PlaylistDetailActivity.viewModel.deleteSong(temp);
                Toast.makeText(this, "Remove from favorite playlist", Toast.LENGTH_SHORT).show();

            }
            for (Song songData : MyService.mData) {
                if (songData.getUrl().equals(song.getUrl())) {
                    songData.isLove = false;
                }
            }
        }
    }

}