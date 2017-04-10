package com.ditek.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ditek.android.popularmovies.FavoritesContract.FavoritesEntry;

/**
 * Created by dj on 3/31/2017.
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 2;

    FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                        FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        FavoritesEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_VOTE + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_BACKDROP + " TEXT NOT NULL " +
                        ");";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}