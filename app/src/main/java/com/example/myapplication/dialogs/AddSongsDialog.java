package com.example.myapplication.dialogs;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.PlaylistDetailActivity;
import com.example.myapplication.activities.SplashActivity;
import com.example.myapplication.adapters.SongAdapter;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;

import java.util.List;

public class AddSongsDialog extends AppCompatDialogFragment {
    List<Song> mData;
    RecyclerView mRecyclerView;
    SongAdapter mSongAdapter;
    Playlist mPlaylist;

    TextView txtNumberOfSongs;
    ImageView imgPlaylist;


    public AddSongsDialog(Playlist mPlaylist, TextView txtNumberOfSongs, ImageView imgPlaylist) {
        this.mPlaylist = mPlaylist;
        this.txtNumberOfSongs = txtNumberOfSongs;
        this.imgPlaylist = imgPlaylist;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_add_songs, container, false);

        mRecyclerView = view.findViewById(R.id.rcv_list_song);
        mData = SplashActivity.mSongList;
        mSongAdapter = new SongAdapter(mData, new SongAdapter.IOnItemClickListener() {
            @Override
            public void onItemClickListener(Song song) {
                handleAddSongToPlaylist(song, mPlaylist);
            }

            @Override
            public void onMoreButtonClickListener(Song song, View view) {

            }
        });

        mRecyclerView.setAdapter(mSongAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);


        return view;
    }

    private void handleAddSongToPlaylist(Song song, Playlist playlist) {
        for (Song temp : AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(playlist.getId())) {
            if (temp.getUrl().equals(song.getUrl())) {
                Toast.makeText(getContext(), R.string.song_exists, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Song newSong = song.copySong();
        newSong.playlistId = playlist.getId();
        PlaylistDetailActivity.viewModel.insertSong(newSong);
        setNumberOfSongs();
        handleImagePlaylist();
        dismiss();
    }

    private void setNumberOfSongs() {
        if (AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(mPlaylist.getId()).size() == 0 || AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(mPlaylist.getId()).size() == 1) {
            txtNumberOfSongs.setText(AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(mPlaylist.getId()).size() + " " + getContext().getString(R.string.song));
        } else {
            txtNumberOfSongs.setText(AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(mPlaylist.getId()).size() + " " + getContext().getString(R.string.songs));
        }
    }

    private void handleImagePlaylist() {
        if (mPlaylist.getId() == 1) {
            imgPlaylist.setImageResource(R.drawable.favorite_playlist);
        } else {
            if (AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(mPlaylist.getId()).size() > 0) {
                Log.v("imgPlaylist", mData.size() + "");
                for (Song song : AppDatabase.getInstance(getContext()).playlistDao().getAllSongsInPlaylist(mPlaylist.getId())) {
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
}
