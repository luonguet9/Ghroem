package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ViewPagerAdapter;
import com.example.myapplication.models.Song;
import com.example.myapplication.services.MyService;
import com.example.myapplication.utilities.DepthPageTransformer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    MyService mService;
    boolean isServiceConnected;
    BroadcastReceiver mBroadcastReceiver;

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder myBinder = (MyService.MyBinder) iBinder;
            mService = myBinder.getMyService();
            isServiceConnected = true;

            handleBottomMediaPlayer();

            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int action = intent.getIntExtra("action_music", 0);
                    handleActionBottomMediaPlayer(action);
                }
            };

            LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mBroadcastReceiver, new IntentFilter("send_action_to_activity"));

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            isServiceConnected = false;
        }
    };

    BottomNavigationView mBottomNavigationView;
    ViewPager2 mViewPager2;

    //bottom media player
    RelativeLayout layoutBottomMediaPlayer;
    ImageView imgSong;
    TextView txtNameSong, txtSingerSong;
    ImageView imgPrevious, imgPlayOrPause, imgNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        layoutBottomMediaPlayer.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MediaPlayerActivity.class));
        });

        onClickStartService();

    }

    private void initView() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mViewPager2 = findViewById(R.id.view_pager2);
        mViewPager2.setAdapter(new ViewPagerAdapter(this));
        mViewPager2.setPageTransformer(new DepthPageTransformer());
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.item_home).setChecked(true);
                        break;

                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.item_library).setChecked(true);
                        break;

                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.item_setting).setChecked(true);
                        break;
                }
            }
        });

        mBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_home:
                    mViewPager2.setCurrentItem(0);
                    break;

                case R.id.item_library:
                    mViewPager2.setCurrentItem(1);
                    break;

                case R.id.item_setting:
                    mViewPager2.setCurrentItem(2);
                    break;
            }
            return true;
        });

        //bottom media music
        layoutBottomMediaPlayer = findViewById(R.id.bottom_media_player);
        imgSong = findViewById(R.id.image_song);
        txtNameSong = findViewById(R.id.text_name_song);
        txtSingerSong = findViewById(R.id.text_singer_song);
        imgPrevious = findViewById(R.id.image_previous);
        imgPlayOrPause = findViewById(R.id.image_play_or_pause);
        imgNext = findViewById(R.id.image_next);
    }

    private void handleBottomMediaPlayer() {
        layoutBottomMediaPlayer.setVisibility(View.VISIBLE);
        txtNameSong.setText(mService.mSong.getName());
        txtSingerSong.setText(mService.mSong.getSinger());
        setStatusPlayOrPause();

        imgPlayOrPause.setOnClickListener(view -> {
            if (mService.isPlaying) {
                mService.pauseMusic();
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

    private void handleActionBottomMediaPlayer(int action) {
        switch (action) {
            case MyService.ACTION_START:
                layoutBottomMediaPlayer.setVisibility(View.VISIBLE);
                handleBottomMediaPlayer();
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

    private void onClickStartService() {
        Intent intent = new Intent(this, MyService.class);

        Bundle bundle = new Bundle();
        Song song = new Song("Love Me Like You Do", "Ellie Goulding",R.drawable.love, R.raw.music);
        bundle.putSerializable("object_song", song);
        intent.putExtras(bundle);

        startService(intent);

        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void onClickStopService() {
        if (isServiceConnected) {
            unbindService(mServiceConnection);
            isServiceConnected = false;
        }

        stopService(new Intent(this, MyService.class));

        layoutBottomMediaPlayer.setVisibility(View.GONE);
    }

    private void setStatusPlayOrPause() {
        if (mService == null) {
            return;
        }

        if (mService.isPlaying) {
            imgPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24);
        } else {
            imgPlayOrPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music_service", action);
        startService(intent);
    }
}