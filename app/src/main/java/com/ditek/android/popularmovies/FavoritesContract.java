package com.ditek.android.popularmovies;

import android.provider.BaseColumns;

/**
 * Created by dj on 3/31/2017.
 */

public class FavoritesContract {
    private FavoritesContract(){}

    public static final class FavoritesEntry implements BaseColumns{
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_DATE = "release_data";
        public static final String COLUMN_VOTE = "avg_vote";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_POSTER = "poster_path";
//        public static final String COLUMN_ = "";
//        public static final String COLUMN_ = "";

    }
}
