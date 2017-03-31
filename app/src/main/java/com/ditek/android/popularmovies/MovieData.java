package com.ditek.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.parceler.ParcelConstructor;

import java.io.Serializable;
import java.util.ArrayList;

import static android.R.attr.data;

/**
 * Created by diaa on 2/10/2017.
 */

@org.parceler.Parcel
public class MovieData{
    String title;
    int id;
    String releaseDate;
    String voteAvg;
    String plot;
    String posterPath;

    @ParcelConstructor
    public MovieData(String title, String releaseDate, String posterPath, String voteAvg, String plot, int id) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAvg = voteAvg;
        this.plot = plot;
        this.id = id;
    }
}