package com.ditek.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diaa on 2/8/2017.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    List<Integer> mIconsList;
    ImageView mIconView;

    MovieListAdapter(List<Integer> data){
        mIconsList = new ArrayList<>(data);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        mIconView.setImageResource(mIconsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mIconsList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        public MovieViewHolder(View view){
            super(view);
            mIconView = (ImageView) view.findViewById(R.id.iv_icon);
        }
    }
}
