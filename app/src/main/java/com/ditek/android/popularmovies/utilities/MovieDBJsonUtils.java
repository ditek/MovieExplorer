package com.ditek.android.popularmovies.utilities;

import com.ditek.android.popularmovies.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle TheMovieDB JSON data.
 */
public final class MovieDBJsonUtils {

    /**
     * This method parses JSON from a web response and returns a list of MovieData
     *
     * @param inputJsonStr JSON response from server
     * @return A list of MovieData objects
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<MovieData> getMovieListFromJson(String inputJsonStr)
            throws JSONException {

        /* Weather information. Each day's forecast info is an element of the "list" array */
        final String DB_RESULTS = "results";
        final String DB_POSTER_PATH = "poster_path";
        final String DB_TITLE = "title";
        final String DB_RELEASE_DATE = "release_date";
        final String DB_VOTE_AVG = "vote_average";
        final String DB_PLOT = "overview";

        /* String array to hold each movie String */
        List<MovieData> parsedMovieData = new ArrayList<>();

        JSONObject inputJson = new JSONObject(inputJsonStr);

        JSONArray resultsArray = inputJson.getJSONArray(DB_RESULTS);

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject movieObject = resultsArray.getJSONObject(i);

            String posterFile = movieObject.getString(DB_POSTER_PATH);
            String posterPath = Utilities.buildImageUrl(posterFile);
            String title = movieObject.getString(DB_TITLE);
            String releaseDate = movieObject.getString(DB_RELEASE_DATE);
            String voteAvg = movieObject.getString(DB_VOTE_AVG);
            String plot = movieObject.getString(DB_PLOT);

            MovieData movieData = new MovieData(title, releaseDate, posterPath, voteAvg, plot);

            parsedMovieData.add(movieData);
        }

        return parsedMovieData;
    }
}