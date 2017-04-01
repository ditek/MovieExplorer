package com.ditek.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Parcelable;
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

public class MainActivity extends AppCompatActivity implements MovieListAdapter.ItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int IMAGE_VIEW_WIDTH = 100;
    private static final String MOVIE_DATA_KEY = "movie_data";
    private static final String SORT_METHOD_KEY = "sort_mode";

    @BindView(R.id.rv_movies) RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    private MovieListAdapter mAdapter;

    private Snackbar mSnackbar;

    private MenuItem mMenuItemPopular;
    private MenuItem mMenuItemTopRated;
    private MenuItem mMenuItemFavorite;

    private List<MovieData> mMovieList;
    private SQLiteDatabase mDb;
    private Utilities.SortMethod mSortMethod = Utilities.SortMethod.POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        int mNoOfColumns = Utilities.calculateNoOfColumns(getApplicationContext(), IMAGE_VIEW_WIDTH);
        GridLayoutManager layoutManager = new GridLayoutManager(this, mNoOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MovieListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mSnackbar = createRetrySnackbar();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIE_DATA_KEY)) {
                mMovieList = Parcels.unwrap(savedInstanceState.getParcelable(MOVIE_DATA_KEY));
                mAdapter.setData(mMovieList);
            }
            if (savedInstanceState.containsKey(SORT_METHOD_KEY)) {
                mSortMethod = Utilities.SortMethod.valueOf(savedInstanceState.getString(SORT_METHOD_KEY));
            }
        } else {
            new GetMoviesTask().execute(mSortMethod);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable listParcelable = Parcels.wrap(mMovieList);
        outState.putParcelable(MOVIE_DATA_KEY, listParcelable);
        outState.putString(SORT_METHOD_KEY, mSortMethod.name());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSortMethod == Utilities.SortMethod.FAVORITE) {
            showMovies(true);
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.i(TAG, mMovieList.get(clickedItemIndex).title);

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("MovieData", Parcels.wrap(mMovieList.get(clickedItemIndex)));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        mMenuItemPopular = menu.findItem(R.id.action_popular);
        mMenuItemTopRated = menu.findItem(R.id.action_top_rated);
        mMenuItemFavorite = menu.findItem(R.id.action_favorite);
        showMovies(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_popular:
                mSortMethod = Utilities.SortMethod.POPULAR;
                break;
            case R.id.action_top_rated:
                mSortMethod = Utilities.SortMethod.TOP_RATED;
                break;
            case R.id.action_favorite:
                mSortMethod = Utilities.SortMethod.FAVORITE;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        showMovies(true);
        showMoviesData();
        return true;
    }

    private void showMovies(boolean getMovies){
        MenuItem selectedItem;
        switch (mSortMethod){
            case POPULAR:
                selectedItem = mMenuItemPopular;
                break;
            case TOP_RATED:
                selectedItem = mMenuItemTopRated;
                break;
            case FAVORITE:
                selectedItem = mMenuItemFavorite;
                break;
            default:
                return;
        }
        if(getMovies){
            new GetMoviesTask().execute(mSortMethod);
        }
        disableAllMenuItems();
        selectedItem.setEnabled(false);
        selectedItem.setCheckable(true);
    }

    private void disableAllMenuItems() {
        mMenuItemPopular.setEnabled(true);
        mMenuItemTopRated.setEnabled(true);
        mMenuItemFavorite.setEnabled(true);
        mMenuItemPopular.setCheckable(false);
        mMenuItemTopRated.setCheckable(false);
        mMenuItemFavorite.setCheckable(false);
    }

    private void showMoviesData() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private Snackbar createRetrySnackbar() {
        return Snackbar.make(findViewById(R.id.main_layout), "No Internet", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSnackbar.dismiss();
                        showMovies(true);
                    }
                });
    }

    private Cursor getFavoriteMovies() {
        return  getContentResolver().query(FavoritesEntry.CONTENT_URI, null, null, null, null);
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
            if (movieSortMethod == Utilities.SortMethod.FAVORITE) {
                Cursor cursor = getFavoriteMovies();
                List<MovieData> movieData = new ArrayList<>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID));
                    String title = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE));
                    String releaseDate = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_DATE));
                    String voteAvg = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_VOTE));
                    String plot = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_PLOT));
                    String posterPath = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_POSTER));
                    movieData.add(new MovieData(title, releaseDate, posterPath, voteAvg, plot, id));
                }
                cursor.close();
                return movieData;
            } else {
                URL requestUrl = Utilities.buildQueryUrl(movieSortMethod, 1);

                try {
                    String response = Utilities.getResponseFromHttpUrl(requestUrl);
                    List<MovieData> movieData = Utilities.getMovieListFromJson(response);
                    Log.i(TAG, "Received data entries: " + String.valueOf(movieData.size()));
                    return movieData;

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
        }

        @Override
        protected void onPostExecute(List<MovieData> results) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (results != null) {
                mMovieList = new ArrayList<>();
                for (MovieData result : results) {
                    mMovieList.add(result);
                }
                mAdapter.setData(mMovieList);
                showMoviesData();
            } else {
                Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " No Data");
                showErrorMessage();
                mSnackbar = createRetrySnackbar();
                mSnackbar.show();
            }
        }
    }
}
