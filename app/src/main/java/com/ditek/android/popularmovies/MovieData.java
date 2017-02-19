package com.ditek.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import static android.R.attr.data;

/**
 * Created by diaa on 2/10/2017.
 */

public class MovieData implements Parcelable {
    String title;
    String releaseDate;
    String posterPath;
    String voteAvg;
    String plot;

    public MovieData(String title, String releaseDate, String posterPath, String voteAvg, String plot) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAvg = voteAvg;
        this.plot = plot;
    }

    private MovieData(Parcel in) {
        this.title = in.readString();
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        this.voteAvg = in.readString();
        this.plot = in.readString();
    }

    public static final Parcelable.Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel source) {
            return new MovieData(source);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeString(voteAvg);
        dest.writeString(plot);
    }
}