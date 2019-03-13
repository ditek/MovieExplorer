package com.ditek.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private List<MovieData> mMovieDataList;
    final private ItemClickListener mOnClickListener;

    MovieListAdapter(ItemClickListener listener) {
        mOnClickListener = listener;
    }

    public interface ItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public void setData(List<MovieData> data) {
        mMovieDataList = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (mMovieDataList != null && mMovieDataList.size() > position) {
            Picasso.with(holder.mPoster.getContext())
                    .load(mMovieDataList.get(position).fullPosterPath)
                    .into(holder.mPoster);
        }
    }

    @Override
    public int getItemCount() {
        if (mMovieDataList != null)
            return mMovieDataList.size();
        else
            return 0;
    }

    /************************************ ViewHolder Class **************************************/

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mPoster;

        public MovieViewHolder(View view) {
            super(view);
            mPoster = (ImageView) view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
