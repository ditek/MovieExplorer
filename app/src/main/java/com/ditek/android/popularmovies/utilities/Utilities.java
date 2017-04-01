package com.ditek.android.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ditek.android.popularmovies.MovieData;
import com.ditek.android.popularmovies.Review;
import com.ditek.android.popularmovies.Trailer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185/";

    private static final String VIDEO_BASE_URL = "https://www.youtube.com/watch";

    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";

    private static final String VIDEO_PATH = "videos";
    private static final String REVIEW_PATH = "reviews";

    private static final String MOVIE_SORT_POPULAR = "popular";
    private static final String MOVIE_SORT_TOP_RATED = "top_rated";

    private static final String API_KEY_PARAM = "api_key";
    private static final String API_KEY = "10c850a953edc25fcaa42e84034e18cb";

    private static final String PAGE_PARAM = "page";

    public static List<Trailer> getTrailerListFromJson(String inputJsonStr)
            throws JSONException {
        final String DB_RESULTS = "results";
        List<Trailer> trailerList = new ArrayList<>();
        JSONObject inputJson = new JSONObject(inputJsonStr);
        JSONArray resultsArray = inputJson.getJSONArray(DB_RESULTS);

        // Use lambda expression instead of the for loop
//        Gson gson = new Gson();
//        Type listType = new TypeToken<List<Trailer>>() {}.getType();
//        trailerList = gson.fromJson(resultsArray.toString(), listType);
//        trailerList.removeIf(x -> !x.getType().equals("Trailer"));

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject trailerObject = resultsArray.getJSONObject(i);

            Gson gson = new Gson();
            Trailer trailer = gson.fromJson(trailerObject.toString(), Trailer.class);
            if(trailer.getType().equals("Trailer")) {
                trailerList.add(trailer);
            }
        }
        return trailerList;
    }
    
    public static List<Review> getReviewsListFromJson(String inputJsonStr)
            throws JSONException {
        final String DB_RESULTS = "results";

        JSONObject inputJson = new JSONObject(inputJsonStr);
        JSONArray resultsArray = inputJson.getJSONArray(DB_RESULTS);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Review>>() {}.getType();
        return gson.fromJson(resultsArray.toString(), listType);
    }

    /**
     * This method parses JSON from a web response and returns a list of MovieData
     *
     * @param inputJsonStr JSON response from server
     * @return A list of MovieData objects
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<MovieData> getMovieListFromJson(String inputJsonStr)
            throws JSONException {
        final String DB_RESULTS = "results";
        JSONObject inputJson = new JSONObject(inputJsonStr);
        JSONArray resultsArray = inputJson.getJSONArray(DB_RESULTS);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<MovieData>>() {}.getType();
        List<MovieData> parsedMovieData = gson.fromJson(resultsArray.toString(), listType);

        for (MovieData movieData : parsedMovieData) {
            String fullPosterPath = buildImageUrl(movieData.getPosterPath());
            movieData.setFullPosterPath(fullPosterPath);
        }

        return parsedMovieData;
    }

    //______________________________________________________________________________________________

    public enum SortMethod {POPULAR, TOP_RATED, FAVORITE}

    public static URL buildQueryUrl(SortMethod movieSortMethod, int page) {
        String sortString;
        switch (movieSortMethod) {
            case POPULAR:
                sortString = MOVIE_SORT_POPULAR;
                break;
            case TOP_RATED:
                sortString = MOVIE_SORT_TOP_RATED;
                break;
            default:
                return null;
        }
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(sortString)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Built URI " + url);
        return url;
    }

    public static String buildImageUrl(String imageFileName) {
        String imageUrl = IMAGE_BASE_URL + IMAGE_SIZE + imageFileName;
        return imageUrl;
    }

    public static URL buildVideoQueryUrl(int movieId) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(VIDEO_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Built video URI " + url);
        return url;
    }

    public static URL buildReviewQueryUrl(int movieId) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(REVIEW_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Built review URI " + url);
        return url;
    }

    public static URL buildYoutubeUrl(String videoKey) {
        Uri builtUri = Uri.parse(VIDEO_BASE_URL).buildUpon()
                .appendQueryParameter("v", videoKey)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Built youtube URI " + url);
        return url;
    }

    private static final OkHttpClient client = new OkHttpClient();

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Request request = new Request.Builder()
                .url(url.toString())
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return response.body().string();
    }

    /**
     * Calculates the number of columns that fit on the screen
     *
     * @param context
     * @param imageViewWidth
     */
    public static int calculateNoOfColumns(Context context, int imageViewWidth) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / imageViewWidth);
        return noOfColumns;
    }
}