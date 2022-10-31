package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.models.Song;

import java.util.List;

public class PlaylistViewModel extends AndroidViewModel {

    MutableLiveData<List<Song>> mListSong;
    AppDatabase appDatabase;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        mListSong = new MutableLiveData<>();
        appDatabase = AppDatabase.getInstance(getApplication().getApplicationContext());
    }

    public MutableLiveData<List<Song>> getListSong() {
        return mListSong;
    }

    public void getAllSongList(int playlistId) {
        List<Song> list = appDatabase.playlistDao().getAllSongsInPlaylist(playlistId);
        if (list.size() > 0) {
            mListSong.postValue(list);
        } else {
            mListSong.postValue(null);
        }
    }

    public void insertSong(Song song) {
        appDatabase.playlistDao().insertSong(song);
        getAllSongList(song.playlistId);
    }

    public void deleteSong(Song song) {
        appDatabase.playlistDao().deleteSong(song);
        getAllSongList(song.playlistId);
    }
}
