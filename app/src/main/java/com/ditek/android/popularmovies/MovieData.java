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
    @SerializedName("backdrop_path")
    String backdropPath;
    String fullPosterPath;
    String fullBackdropPath;

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setFullPosterPath(String fullPosterPath) {
        this.fullPosterPath = fullPosterPath;
    }

    public void setFullBackdropPath(String fullBackdropPath) {
        this.fullBackdropPath = fullBackdropPath;
    }

    @ParcelConstructor
    public MovieData(String title, String releaseDate, String fullPosterPath, String fullBackdropPath, String voteAverage, String plot, int id) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.fullPosterPath = fullPosterPath;
        this.fullBackdropPath = fullBackdropPath;
        this.voteAverage = voteAverage;
        this.plot = plot;
        this.id = id;
    }
}