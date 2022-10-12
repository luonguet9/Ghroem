package com.example.myapplication.models;

import java.io.Serializable;

public class Song implements Serializable {
    private String name;
    private String singer;
    private int imageResource;
    private int resource;

    public Song(String name, String singer, int imageResource, int resource) {
        this.name = name;
        this.singer = singer;
        this.imageResource = imageResource;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
