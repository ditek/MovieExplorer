package com.ditek.android.popularmovies;

import java.io.Serializable;
import java.util.ArrayList;

import static android.R.attr.data;

/**
 * Created by diaa on 2/10/2017.
 */

public class MovieData implements Serializable {
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
}
