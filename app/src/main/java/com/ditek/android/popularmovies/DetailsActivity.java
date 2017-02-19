package com.ditek.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private MovieData mMovieData;

    private ImageView mPosterImageView;
    private TextView mTitleTV;
    private TextView mReleaseDataTV;
    private TextView mVoteAvgTV;
    private TextView mPlotTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mTitleTV = (TextView) findViewById(R.id.tv_title);
        mReleaseDataTV = (TextView) findViewById(R.id.tv_release_date);
        mVoteAvgTV = (TextView) findViewById(R.id.tv_vote_avg);
        mPlotTV = (TextView) findViewById(R.id.tv_plot);
        mPosterImageView = (ImageView) findViewById(R.id.iv_details_poster);

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
        }
    }
}
