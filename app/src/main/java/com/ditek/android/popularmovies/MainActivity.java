package com.ditek.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ditek.android.popularmovies.utilities.Utilities;
import com.ditek.android.popularmovies.utilities.MovieDBJsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.ItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String SER_KEY = "com.ditek.android.popularmovies.ser";
    private final static int IMAGE_VIEW_WIDTH = 100;

    private RecyclerView mRecyclerView;
    private MovieListAdapter mAdapter;
    private ProgressBar mLoadingIndicator;
    private Snackbar mSnackbar;

    private TextView mErrorText;

    private MenuItem mMenuItemPopular;
    private MenuItem mMenuItemTopRated;

    private List<MovieData> mIconUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorText = (TextView) findViewById(R.id.tv_error);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        int mNoOfColumns = Utilities.calculateNoOfColumns(getApplicationContext(), IMAGE_VIEW_WIDTH);
        GridLayoutManager layoutManager = new GridLayoutManager(this, mNoOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MovieListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        new GetMoviesTask().execute(Utilities.SortMethod.POPULAR);
        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        mSnackbar = createRetrySnackbar();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.i(TAG, mIconUrlList.get(clickedItemIndex).title);

        Intent intent = new Intent(this, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SER_KEY, mIconUrlList.get(clickedItemIndex));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        mMenuItemPopular = menu.findItem(R.id.action_popular);
        mMenuItemTopRated = menu.findItem(R.id.action_top_rated);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_popular:
                showPopularMovies();
                break;
            case R.id.action_top_rated:
                showTopRatedMovies();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        showMoviesData();
        return true;
    }

    private void showPopularMovies() {
        new GetMoviesTask().execute(Utilities.SortMethod.POPULAR);
        mMenuItemPopular.setEnabled(false);
        mMenuItemTopRated.setCheckable(true);
        mMenuItemTopRated.setEnabled(true);
        mMenuItemTopRated.setCheckable(false);
    }

    private void showTopRatedMovies() {
        new GetMoviesTask().execute(Utilities.SortMethod.TOP_RATED);
        mMenuItemPopular.setEnabled(true);
        mMenuItemPopular.setCheckable(false);
        mMenuItemTopRated.setEnabled(false);
        mMenuItemTopRated.setCheckable(true);
    }

    private void showMoviesData() {
        mErrorText.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
//        mErrorText.setVisibility(View.VISIBLE);
    }

    private Snackbar createRetrySnackbar(){
        return Snackbar.make(findViewById(R.id.main_layout), "No Internet", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSnackbar.dismiss();
                        showPopularMovies();
                    }
                });
    }

    /**********************/
    /** Async Task ********/
    /**********************/
    public class GetMoviesTask extends AsyncTask<Utilities.SortMethod, Void, List<MovieData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieData> doInBackground(Utilities.SortMethod... params) {
            Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + params[0].toString());
            if (params.length == 0) {
                return null;
            }

            Utilities.SortMethod movieSortMethod = params[0];
            URL requestUrl = Utilities.buildQueryUrl(movieSortMethod);

            try {
                String response = Utilities.getResponseFromHttpUrl(requestUrl);
                List<MovieData> movieData = MovieDBJsonUtils.getMovieListFromJson(response);
                Log.i(TAG, "Received data entries: " + String.valueOf(movieData.size()));
                return movieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MovieData> results) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (results != null) {
                mIconUrlList = new ArrayList<>();
                for (MovieData result : results) {
                    mIconUrlList.add(result);
                }
                mAdapter.setData(mIconUrlList);
                showMoviesData();
            } else {
                Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() +  " No Data");
                showErrorMessage();
                mSnackbar = createRetrySnackbar();
                mSnackbar.show();
            }
        }
    }
}
