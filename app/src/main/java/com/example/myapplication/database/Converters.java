package com.example.myapplication.database;

import android.net.Uri;

import androidx.room.TypeConverter;

import com.example.myapplication.models.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<Song> fromString(String value) {
        Type listType = new TypeToken<List<Song>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<Song> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
    @TypeConverter
    public static Uri fromStringToUri(String value) {
//        Type listType = new TypeToken<Uri>() {}.getType();
//        return new Gson().fromJson(value, listType);
        return Uri.parse(value);
    }

    @TypeConverter
    public static String fromUri(Uri uri) {
//        Gson gson = new Gson();
//        return gson.toJson(uri);
        return uri.toString();
    }
}
