package com.ditek.android.popularmovies;

import org.parceler.ParcelConstructor;

/**
 * Created by diaa on 2/10/2017.
 */

@org.parceler.Parcel
public class MovieData {
    int id;
    String title;
    String release_date;
    String vote_average;
    String overview;
    String fullPosterPath;
    String poster_path;

    public String getPosterPath() {
        return poster_path;
    }

    public void setFullPosterPath(String fullPosterPath) {
        this.fullPosterPath = fullPosterPath;
    }

    @ParcelConstructor
    public MovieData(String title, String release_date, String fullPosterPath, String vote_average, String overview, int id) {
        this.title = title;
        this.release_date = release_date;
        this.fullPosterPath = fullPosterPath;
        this.vote_average = vote_average;
        this.overview = overview;
        this.id = id;
    }
}