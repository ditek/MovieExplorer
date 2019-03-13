package com.ditek.android.popularmovies;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;

/**
 * Created by diaa on 3/30/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        // Enable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
