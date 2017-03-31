package com.ditek.android.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by diaa on 3/30/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
