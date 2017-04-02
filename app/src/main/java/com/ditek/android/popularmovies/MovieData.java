package com.ditek.android.popularmovies;

import com.google.gson.annotations.SerializedName;

import org.parceler.ParcelConstructor;

/**
 * Created by diaa on 2/10/2017.
 */

@org.parceler.Parcel
public class MovieData {
    int id;
    String title;
    @SerializedName("release_date")
    String releaseDate;
    @SerializedName("vote_average")
    String voteAverage;
    @SerializedName("overview")
    String plot;
    @SerializedName("poster_path")
    String posterPath;
    String fullPosterPath;

    public String getPosterPath() {
        return posterPath;
    }

    public void setFullPosterPath(String fullPosterPath) {
        this.fullPosterPath = fullPosterPath;
    }

    @ParcelConstructor
    public MovieData(String title, String releaseDate, String fullPosterPath, String voteAverage, String plot, int id) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.fullPosterPath = fullPosterPath;
        this.voteAverage = voteAverage;
        this.plot = plot;
        this.id = id;
    }
}