package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapters.SongAdapter;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.dialogs.AddSongsDialog;
import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;
import com.example.myapplication.services.MyService;
import com.example.myapplication.viewmodel.PlaylistViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {

    ImageView imgBack, imgPlaylist;
    TextView txtPlaylistName, txtNumberOfSongs,txtAddSongs, txtSort;
    RecyclerView mRecyclerView;
    SongAdapter mSongAdapter;
    Playlist mPlaylist;
    List<Song> mData = new ArrayList<>();

    public static PlaylistViewModel viewModel;
    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        initView();
        initViewModel();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPlaylist = (Playlist) bundle.get("playlist_object");
        }

        mData = AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(mPlaylist.getId());

        viewModel.getAllSongList(mPlaylist.getId());

        imgBack.setOnClickListener(view -> {
            onBackPressed();
        });

        txtPlaylistName.setText(mPlaylist.getName());

        handleImagePlaylist();

        mSongAdapter = new SongAdapter(mData, new SongAdapter.IOnItemClickListener() {
            @Override
            public void onItemClickListener(Song song) {
                startMusic(song);
            }

            @Override
            public void onMoreButtonClickListener(Song song, View view) {
                ImageView imgMore = view.findViewById(R.id.image_more);
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), imgMore);
                popupMenu.getMenuInflater().inflate(R.menu.menu_more_song, popupMenu.getMenu());
                MenuItem menuItemLove = popupMenu.getMenu().findItem(R.id.action_love);
                MenuItem menuItemUnLove = popupMenu.getMenu().findItem(R.id.action_un_love);
                MenuItem menuItemAdd = popupMenu.getMenu().findItem(R.id.action_add_to_playlist);
                menuItemLove.setVisible(false);
                menuItemUnLove.setVisible(false);
                menuItemAdd.setVisible(false);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.action_delete_from_playlist) {
                        deleteSongFromPlaylist(song);
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            }
        });

        mRecyclerView.setAdapter(mSongAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        setNumberOfSongs();

        txtSort.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, txtSort);
            popupMenu.getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.sort_title:
                        sortByTitle(mData);
                        loadDataWithAnimation();
                        MyService.mData = mData;
                        return true;

                    case R.id.sort_artist:
                        sortByArtist(mData);
                        loadDataWithAnimation();
                        MyService.mData = mData;
                        return true;

                    case R.id.sort_added_time:
                        mData = AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(mPlaylist.getId());
                        Collections.reverse(mData);
                        loadDataWithAnimation();
                        MyService.mData = mData;
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });

        txtAddSongs.setOnClickListener(view -> {
            AddSongsDialog addSongsDialog = new AddSongsDialog(mPlaylist, txtNumberOfSongs, imgPlaylist);
            addSongsDialog.show(this.getSupportFragmentManager(), "");
            handleImagePlaylist();
        });

    }


    private void initView() {
        imgBack = findViewById(R.id.image_back);
        imgPlaylist = findViewById(R.id.image_playlist_activity);
        txtSort = findViewById(R.id.txt_sort);
        txtNumberOfSongs = findViewById(R.id.txt_number_of_songs);
        txtPlaylistName = findViewById(R.id.txt_name_playlist);
        txtAddSongs = findViewById(R.id.txt_add_songs);
        mRecyclerView = findViewById(R.id.rcv_list_song);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        viewModel.getListSong().observe(this, list ->  {
            if (list == null) {
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mData = list;
                //Collections.reverse(mData);
                mSongAdapter.setData(mData);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }


    public void onClickStartService(Song song) {
        Intent intent = new Intent(this, MyService.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", song);
        intent.putExtras(bundle);

        startService(intent);

        //bindService(intent, mainActivity.mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onClickStopService() {
        if (MainActivity.isServiceConnected) {
            //unbindService(mainActivity.mServiceConnection);
            MainActivity.isServiceConnected = false;
        }

        stopService(new Intent(this, MyService.class));

    }

    private void startMusic(Song song) {
        MyService.mSong = song;
        MyService.mData = AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(mPlaylist.getId());
        onClickStopService();
        onClickStartService(MyService.mSong);
    }

    private void setNumberOfSongs() {
        if (AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(mPlaylist.getId()).size() == 0 || AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(mPlaylist.getId()).size() == 1) {
            txtNumberOfSongs.setText(AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(mPlaylist.getId()).size() + " song");
        } else {
            txtNumberOfSongs.setText(AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(mPlaylist.getId()).size() + " songs");
        }
    }

    private void handleImagePlaylist() {
        if (mPlaylist.getId() == 1) {
            imgPlaylist.setImageResource(R.drawable.favorite_playlist);
        } else {
            if (AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(mPlaylist.getId()).size() > 0) {
                Log.v("imgPlaylist", mData.size() + "");
                for (Song song : AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(mPlaylist.getId())) {
                    Uri artworkUri = song.getAlbumUri();
                    if (artworkUri != null) {
                        imgPlaylist.setImageURI(artworkUri);
                        break;
                    }
                }
            } else {
                imgPlaylist.setImageResource(R.drawable.playlist_cover);
            }
        }
    }

    private void sortByTitle(List<Song> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(Song::getName));
        }
    }

    private void sortByArtist(List<Song> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(Song::getSinger));
        }
    }

    private void loadDataWithAnimation(){
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_up_to_down);
        mRecyclerView.setLayoutAnimation(layoutAnimationController);
        mSongAdapter.setData(mData);
    }

    private void addSongToPlaylist(Song song){
        Song newSong = song.copySong();
        if (mPlaylist.getName().equals("Favorite")) {
            song.isLove = true;
            newSong.isLove = true;
        }
        for(Song temp : mData) {
            if (temp.getUrl().equals(newSong.getUrl())) {
                Toast.makeText(this, "This song has exists!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        newSong.playlistId = mPlaylist.getId();
        viewModel.insertSong(newSong);
        setNumberOfSongs();
        handleImagePlaylist();
    }

    private void deleteSongFromPlaylist(Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete this song")
                .setMessage("Are you sure to delete?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    for (Song temp : AppDatabase.getInstance(this).playlistDao().getAllSongsInPlaylist(mPlaylist.getId())) {
                        if (temp.getUrl().equals(song.getUrl())) {
                            viewModel.deleteSong(temp);
                        }
                        if (MyService.mSong != null && MyService.mSong.getUrl().equals(song.getUrl())) {
                            MyService.mSong.isLove = false;
                        }

                        for (Song songData : MyService.mData) {
                            if (songData.getUrl().equals(song.getUrl())) {
                                songData.isLove = false;
                            }
                        }
                    }
                    handleImagePlaylist();
                    setNumberOfSongs();
                })
                .show();
    }

}