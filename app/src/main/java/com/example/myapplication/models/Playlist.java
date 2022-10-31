package com.example.myapplication.models;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "playlist")
public class Playlist implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    //private List<Song> songList = new ArrayList<>();


    public Playlist(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


//    public List<Song> getSongList() {
//        return songList;
//    }
//
//    public void setSongList(List<Song> songList) {
//        this.songList = songList;
//    }
}
