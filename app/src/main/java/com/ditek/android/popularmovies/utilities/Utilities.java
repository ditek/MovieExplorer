package com.ditek.android.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ditek.android.popularmovies.MovieData;
import com.ditek.android.popularmovies.Review;
import com.ditek.android.popularmovies.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.id;
import static com.ditek.android.popularmovies.utilities.Utilities.SortMethod.POPULAR;
import static com.ditek.android.popularmovies.utilities.Utilities.SortMethod.TOP_RATED;

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
        /* Weather information. Each day's forecast info is an element of the "list" array */
        final String DB_RESULTS = "results";
        final String DB_KEY = "key";
        final String DB_NAME = "name";
        final String DB_TYPE = "type";

        /* String array to hold each movie String */
        List<Trailer> trailerList = new ArrayList<>();

        JSONObject inputJson = new JSONObject(inputJsonStr);

        JSONArray resultsArray = inputJson.getJSONArray(DB_RESULTS);

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject trailerObject = resultsArray.getJSONObject(i);

            String trailerType = trailerObject.getString(DB_TYPE);
            if(trailerType.equals("Trailer")) {
                String trailerKey = trailerObject.getString(DB_KEY);
//                String trailerPath = VIDEO_BASE_URL + trailerKey;
                String name = trailerObject.getString(DB_NAME);
                Trailer trailer = new Trailer(name, trailerKey);
                trailerList.add(trailer);
            }
        }
        return trailerList;
    }
    
    public static List<Review> getReviewsListFromJson(String inputJsonStr)
            throws JSONException {
        /* Weather information. Each day's forecast info is an element of the "list" array */
        final String DB_RESULTS = "results";
        final String DB_AUTHOR = "author";
        final String DB_CONTENT = "content";

        /* String array to hold each movie String */
        List<Review> reviewList = new ArrayList<>();

        JSONObject inputJson = new JSONObject(inputJsonStr);

        JSONArray resultsArray = inputJson.getJSONArray(DB_RESULTS);

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject reviewObject = resultsArray.getJSONObject(i);

            String author = reviewObject.getString(DB_AUTHOR);
            String content = reviewObject.getString(DB_CONTENT);
            Review review = new Review(author, content);
            reviewList.add(review);
        }
        return reviewList;
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
        /* Weather information. Each day's forecast info is an element of the "list" array */
        final String DB_RESULTS = "results";
        final String DB_ID = "id";
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
            String posterPath = buildImageUrl(posterFile);
            String title = movieObject.getString(DB_TITLE);
            String releaseDate = movieObject.getString(DB_RELEASE_DATE);
            String voteAvg = movieObject.getString(DB_VOTE_AVG);
            String plot = movieObject.getString(DB_PLOT);
            int id = movieObject.getInt(DB_ID);

            MovieData movieData = new MovieData(title, releaseDate, posterPath, voteAvg, plot, id);

            parsedMovieData.add(movieData);
        }
        return parsedMovieData;
    }

    //______________________________________________________________________________________________

    public enum SortMethod {POPULAR, TOP_RATED}

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