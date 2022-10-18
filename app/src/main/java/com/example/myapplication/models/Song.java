package com.example.myapplication.models;


import java.io.Serializable;

public class Song implements Serializable {
    private int id;
    private String name;
    private String album;
    private int imageResource;
    private String singer;
    private String url;
    private String addedDate;


    public Song(int id, String name, String album, int imageResource, String singer, String url, String addedDate) {
        this.id = id;
        this.name = name;
        this.album = album;
        this.imageResource = imageResource;
        this.singer = singer;
        this.url = url;
        this.addedDate = addedDate;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }
}
