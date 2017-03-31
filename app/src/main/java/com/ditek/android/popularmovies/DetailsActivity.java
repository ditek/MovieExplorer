package com.ditek.android.popularmovies;

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

    private ImageView mPosterImageView;
    private TextView mTitleTV;
    private TextView mReleaseDataTV;
    private TextView mVoteAvgTV;
    private TextView mPlotTV;

    private TrailerAdapter mTrailerAdapter;
    @BindView(R.id.rv_trailers)
    RecyclerView mTrailersView;

    private ReveiwAdapter mReviewAdapter;
    @BindView(R.id.rv_reviews)
    RecyclerView mReviewsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        mTitleTV = (TextView) findViewById(R.id.tv_title);
        mReleaseDataTV = (TextView) findViewById(R.id.tv_release_date);
        mVoteAvgTV = (TextView) findViewById(R.id.tv_vote_avg);
        mPlotTV = (TextView) findViewById(R.id.tv_plot);
        mPosterImageView = (ImageView) findViewById(R.id.iv_details_poster);

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
                    .load(mMovieData.posterPath)
                    .into(mPosterImageView);
            mTitleTV.setText(mMovieData.title);
            mReleaseDataTV.setText(mMovieData.releaseDate);
            mVoteAvgTV.setText(mMovieData.voteAvg);
            mPlotTV.setText(mMovieData.plot);
            Log.i(TAG, mMovieData.title);
            new GetTrailersTask().execute(mMovieData.id);
        }
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
