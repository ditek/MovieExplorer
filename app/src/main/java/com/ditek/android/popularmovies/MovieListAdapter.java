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

    private List<MovieData> mIconsList;
    final private ItemClickListener mOnClickListener;

    MovieListAdapter(ItemClickListener listener) {
        mOnClickListener = listener;
    }

    public interface ItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public void setData(List<MovieData> data) {
        mIconsList = new ArrayList<>(data);
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
        if (mIconsList != null && mIconsList.size() > position) {
            Picasso.with(holder.mIconView.getContext())
                    .load(mIconsList.get(position).fullPosterPath)
                    .into(holder.mIconView);
        }
    }

    @Override
    public int getItemCount() {
        if (mIconsList != null)
            return mIconsList.size();
        else
            return 0;
    }

    /**********************/
    /** ViewHolder Class **/
    /**********************/
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mIconView;

        public MovieViewHolder(View view) {
            super(view);
            mIconView = (ImageView) view.findViewById(R.id.iv_icon);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
