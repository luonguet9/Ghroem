package com.example.myapplication.models;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

public class Song implements Serializable, Parcelable {
    private int id;
    private String name;
    private String album;
    private int imageResource;
    private String singer;
    private String url;
    private String addedDate;
    private Uri albumUri;


    public Song(int id, String name, String album, int imageResource, String singer, String url, String addedDate) {
        this.id = id;
        this.name = name;
        this.album = album;
        this.imageResource = imageResource;
        this.singer = singer;
        this.url = url;
        this.addedDate = addedDate;
    }

    public Song(int id, String name, String album, int imageResource, String singer, String url, String addedDate, Uri albumUri) {
        this.id = id;
        this.name = name;
        this.album = album;
        this.imageResource = imageResource;
        this.singer = singer;
        this.url = url;
        this.addedDate = addedDate;
        this.albumUri = albumUri;
    }

    protected Song(Parcel in) {
        id = in.readInt();
        name = in.readString();
        album = in.readString();
        imageResource = in.readInt();
        singer = in.readString();
        url = in.readString();
        addedDate = in.readString();
        albumUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    public Uri getAlbumUri() {
        return albumUri;
    }

    public void setAlbumUri(Uri albumUri) {
        this.albumUri = albumUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(album);
        parcel.writeInt(imageResource);
        parcel.writeString(singer);
        parcel.writeString(url);
        parcel.writeString(addedDate);
        parcel.writeParcelable(albumUri, i);
    }
}
