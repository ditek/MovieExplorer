package com.ditek.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ditek.android.popularmovies.utilities.Utilities;
import com.github.zagum.switchicon.SwitchIconView;
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

import com.ditek.android.popularmovies.FavoritesContract.FavoritesEntry;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private MovieData mMovieData;
    private List<Trailer> mTrailerList;
    private List<Review> mReviewList;
    private SQLiteDatabase mDb;

    @BindView(R.id.tv_title) TextView mTitleTV;
    @BindView(R.id.tv_release_date) TextView mReleaseDataTV;
    @BindView(R.id.tv_vote_avg) TextView mVoteAvgTV;
    @BindView(R.id.tv_plot) TextView mPlotTV;
    @BindView(R.id.iv_details_poster) ImageView mPosterImageView;

    @BindView(R.id.switch_favorite) SwitchIconView mFavoriteSwitch;
    @BindView(R.id.rv_trailers) RecyclerView mTrailersView;
    @BindView(R.id.rv_reviews) RecyclerView mReviewsView;

    private ReveiwAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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
                    .load(mMovieData.fullPosterPath)
                    .into(mPosterImageView);
            mTitleTV.setText(mMovieData.title);
            mReleaseDataTV.setText(mMovieData.releaseDate);
            mVoteAvgTV.setText(mMovieData.voteAverage);
            mPlotTV.setText(mMovieData.plot);

            // Check if the movie is a favorite
            mFavoriteSwitch.setIconEnabled(isFavorite());
            mFavoriteSwitch.setOnClickListener(new View.OnClickListener() {
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
        if (mFavoriteSwitch.isIconEnabled()) {
            deleteFromFavorites();
        } else {
            addToFavorites();
        }
        mFavoriteSwitch.setIconEnabled(!mFavoriteSwitch.isIconEnabled());
    }

    boolean isFavorite() {
        String selection = FavoritesEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {String.valueOf(mMovieData.id)};
        ContentResolver resolver = getContentResolver();

        Cursor cursor = resolver.query(
                FavoritesEntry.CONTENT_URI, null, selection, selectionArgs, null
        );

//        Cursor cursor = mDb.query(
//                FavoritesEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null
//        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows > 0;
    }

    void addToFavorites() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoritesEntry.COLUMN_TITLE, mMovieData.title);
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_ID, mMovieData.id);
        contentValues.put(FavoritesEntry.COLUMN_DATE, mMovieData.releaseDate);
        contentValues.put(FavoritesEntry.COLUMN_VOTE, mMovieData.voteAverage);
        contentValues.put(FavoritesEntry.COLUMN_PLOT, mMovieData.plot);
        contentValues.put(FavoritesEntry.COLUMN_POSTER, mMovieData.fullPosterPath);
//        mDb.insert(FavoritesEntry.TABLE_NAME, null, contentValues);

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

    /* Async Task ********/

    public class GetTrailersTask extends AsyncTask<Integer, Void, Pair<List<Trailer>, List<Review>>> {

        @Override
        protected Pair<List<Trailer>, List<Review>> doInBackground(Integer... params) {
            Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + String.valueOf(params[0]));
            if (params.length == 0) {
                return null;
            }

            int movieId = params[0].intValue();
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
                for (Trailer result : results.first) {
                    mTrailerList.add(result);
                }
            }
            if (results.second != null) {
                for (Review result : results.second) {
                    mReviewList.add(result);
                }
            }
            mTrailerAdapter.setData(mTrailerList);
            mReviewAdapter.setData(mReviewList);
        }
    }
}
