package com.example.myapplication.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.models.Song;
import com.example.myapplication.services.MyService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    ActivityResultLauncher<String> storagePermissionLauncher;
    public static List<Song> mSongList;


    public static int langSelect = -1;
    public static int themeSelect = -1;
    public static String language = Locale.getDefault().getDisplayLanguage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handleLanguageSystem();
        handleThemeSystem();

        storagePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                fetchSongs();
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }, 2000);

            } else {
                respondOnUserPermissionActs();
            }

        });

        storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void handleLanguageSystem() {
        switch (Locale.getDefault().getDisplayLanguage()) {
            case "English":
                SplashActivity.langSelect = 0;
                break;
            case "Tiếng Việt":
                SplashActivity.langSelect = 1;
                break;
        }
    }

    private void handleThemeSystem() {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                SplashActivity.themeSelect = 1;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                SplashActivity.themeSelect = 0;
                break;
        }
    }

    private void fetchSongs() {
        if (MyService.mData != null && !MyService.mData.isEmpty()) {
            return;
        }

        mSongList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String[] projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.Media.ALBUM_ID,
            };

            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

            Uri collection;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            Thread loadingThread = new Thread(() -> {
                Cursor cursor = getApplicationContext()
                        .getContentResolver().query(collection,
                                projection, null, null, sortOrder);
                while (cursor.moveToNext()) {
                    int songId = cursor.getInt(0);
                    String songName = cursor.getString(1);
                    String songAlbum = cursor.getString(2);
                    String songSinger = cursor.getString(3);
                    String songURL = cursor.getString(4);
                    String addedDate = cursor.getString(5);
                    long albumId = cursor.getLong(6);

                    Uri albumartUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
                    Song song = new Song(songId, songName, songAlbum, R.drawable.spotify_blue, songSinger, songURL, addedDate, albumartUri);
                    mSongList.add(song);
                }
                cursor.close();
                //mSongList.sort(Constant.songComparator);
                MyService.mData = new ArrayList<>(mSongList);
                //loadAlbumPicThread.start();
                //loadAlbum.start();
            });

            loadingThread.start();

        }

    }

    private void respondOnUserPermissionActs() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            fetchSongs();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected.
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Requesting Permission");
                alertDialog.setMessage("Allow us to fetch songs form your device");

                    alertDialog.setPositiveButton("Allow", (dialogInterface, i) -> {
                        //request permission again
                        storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    });

                    alertDialog.setNegativeButton("Not Allow", (dialogInterface, i) -> {
                        //request permission again
                        Toast.makeText(this, "You denied to fetch songs", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    });

                alertDialog.show();
            } else {
                Toast.makeText(this, "You denied to fetch songs", Toast.LENGTH_SHORT).show();
                //We can close our application here
            }
        }
    }

}