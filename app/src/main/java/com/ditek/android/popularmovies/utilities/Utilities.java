package com.ditek.android.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185/";

    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";

    private static final String MOVIE_SORT_POPULAR = "popular";
    private static final String MOVIE_SORT_TOP_RATED = "top_rated";

    private static final String API_KEY_PARAM = "api_key";
    private static final String API_KEY = "API_KEY";

    public enum SortMethod {POPULAR, TOP_RATED}

    public static URL buildQueryUrl(SortMethod movieSortMethod) {
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

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static int calculateNoOfColumns(Context context, int imageViewWidth) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / imageViewWidth);
        return noOfColumns;
    }
}