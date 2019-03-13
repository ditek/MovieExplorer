package com.ditek.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ditek.android.popularmovies.FavoritesContract.FavoritesEntry;
import com.ditek.android.popularmovies.utilities.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private MovieData mMovieData;
    private List<Trailer> mTrailerList;
    private List<Review> mReviewList;
    private SQLiteDatabase mDb;

    @BindView(R.id.tv_release_date) TextView mReleaseDataTV;
    @BindView(R.id.tv_movie_name) TextView mMovieTitle;
    @BindView(R.id.tv_vote_avg) TextView mVoteAvgTV;
    @BindView(R.id.ratingBar) RatingBar mRatingBar;
    @BindView(R.id.tv_plot) TextView mPlotTV;
    @BindView(R.id.iv_details_backdrop) ImageView mBackdropImageView;
    @BindView(R.id.poster) ImageView mPosterImageView;

    @BindView(R.id.rv_trailers) RecyclerView mTrailersView;
    @BindView(R.id.rv_reviews) RecyclerView mReviewsView;

    @BindView(R.id.toolbar_details) Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.fab_details) FloatingActionButton mFab;

    private ReveiwAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailersView.setLayoutManager(trailersLayoutManager);
        mTrailersView.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(this);
        mTrailersView.setAdapter(mTrailerAdapter);

        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewsView.setLayoutManager(reviewsLayoutManager);
        mReviewsView.setHasFixedSize(true);
        mReviewAdapter = new ReveiwAdapter();
        mReviewsView.setAdapter(mReviewAdapter);

        mMovieData = Parcels.unwrap(getIntent().getParcelableExtra("MovieData"));
        if (mMovieData != null) {
            Picasso.with(this)
                    .load(mMovieData.fullBackdropPath)
                    .into(mBackdropImageView);
            Picasso.with(this)
                    .load(mMovieData.fullPosterPath)
                    .into(mPosterImageView);
            collapsingToolbar.setTitle(mMovieData.title);
            mMovieTitle.setText(mMovieData.title);
            // Only use the year part of the date
            mReleaseDataTV.setText(mMovieData.releaseDate.substring(0, 4));
            mVoteAvgTV.setText(mMovieData.voteAverage);
            mRatingBar.setRating(Float.valueOf(mMovieData.voteAverage)/2);
            mPlotTV.setText(mMovieData.plot);

            // Check if the movie is a favorite
            setFabIcon(isFavorite());
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteClickHandler(v);
                }
            });

            Log.i(TAG, mMovieData.title);
            new GetTrailersTask().execute(mMovieData.id);
        }
    }

    public void favoriteClickHandler(View view) {
        boolean isFav = isFavorite();
        if (isFav) {
            deleteFromFavorites();
        } else {
            addToFavorites();
        }
        setFabIcon(!isFav);
    }

    boolean isFavorite() {
        String selection = FavoritesEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {String.valueOf(mMovieData.id)};
        ContentResolver resolver = getContentResolver();

        Cursor cursor = resolver.query(
                FavoritesEntry.CONTENT_URI, null, selection, selectionArgs, null
        );

        if(cursor == null){
            return false;
        }
        int numRows = cursor.getCount();
        cursor.close();
        return numRows > 0;
    }

    void setFabIcon(boolean fillIcon){
        mFab.setImageResource(fillIcon ? R.drawable.ic_star_full : R.drawable.ic_star_border);
    }

    void addToFavorites() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoritesEntry.COLUMN_TITLE, mMovieData.title);
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_ID, mMovieData.id);
        contentValues.put(FavoritesEntry.COLUMN_DATE, mMovieData.releaseDate);
        contentValues.put(FavoritesEntry.COLUMN_VOTE, mMovieData.voteAverage);
        contentValues.put(FavoritesEntry.COLUMN_PLOT, mMovieData.plot);
        contentValues.put(FavoritesEntry.COLUMN_POSTER, mMovieData.fullPosterPath);
        contentValues.put(FavoritesEntry.COLUMN_BACKDROP, mMovieData.fullBackdropPath);

        Uri uri = getContentResolver().insert(FavoritesEntry.CONTENT_URI, contentValues);
        Log.i(TAG, "addToFavorites: " + uri.toString());
    }

    void deleteFromFavorites() {
        String selection = FavoritesEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {String.valueOf(mMovieData.id)};
        mDb.delete(FavoritesEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
    }

    /*************************************** Async Task *****************************************/

    public class GetTrailersTask extends AsyncTask<Integer, Void, Pair<List<Trailer>, List<Review>>> {

        @Override
        protected Pair<List<Trailer>, List<Review>> doInBackground(Integer... params) {
            Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + params[0]);
            int movieId = params[0];
            URL trailerRequestUrl = Utilities.buildVideoQueryUrl(movieId);
            URL reviewRequestUrl = Utilities.buildReviewQueryUrl(movieId);

            try {
                String responseTrailers = Utilities.getResponseFromHttpUrl(trailerRequestUrl);
                String responseReviews = Utilities.getResponseFromHttpUrl(reviewRequestUrl);

                List<Trailer> trailers = Utilities.getTrailerListFromJson(responseTrailers);
                List<Review> reviews = Utilities.getReviewsListFromJson(responseReviews);
                Log.i(TAG, "Received data entries: " + String.valueOf(trailers.size()));
                return new Pair<>(trailers, reviews);

            } catch (SocketTimeoutException e) {
                Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + ": " + "Connection Timeout");
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Pair<List<Trailer>, List<Review>> results) {
            if(results == null){
                return;
            }
            mTrailerList = new ArrayList<>();
            mReviewList = new ArrayList<>();
            if (results.second != null) {
                mTrailerList.addAll(results.first);
            }
            if (results.second != null) {
                mReviewList.addAll(results.second);
            }
            mTrailerAdapter.setData(mTrailerList);
            mReviewAdapter.setData(mReviewList);
        }
    }
}
