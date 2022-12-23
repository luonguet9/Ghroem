package com.example.myapplication.database;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String MY_SHARED_PREFERENCES = "MY_SHARED_PREFERENCES";
    private Context mContext;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    public SharedPreferencesManager(Context mContext) {
        this.mContext = mContext;
        mSharedPreferences = mContext.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void putInteger(String key, int i) {
        mEditor.putInt(key, i);
        mEditor.apply();
    }

    public int getInteger(String key) {
        return mSharedPreferences.getInt(key, 0);
    }
}
