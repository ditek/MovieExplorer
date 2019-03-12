package com.ditek.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diaa on 3/30/2017.
 */

public class ReveiwAdapter extends RecyclerView.Adapter<ReveiwAdapter.ReviewViewHolder> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Review> mReviewList;

    ReveiwAdapter() {
    }

    public void setData(List<Review> data) {
        mReviewList = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        if (mReviewList != null && mReviewList.size() > position) {
            holder.mAuthorTextView.setText(mReviewList.get(position).author);
            holder.mContentTextView.setText(mReviewList.get(position).content);
        }
    }

    @Override
    public int getItemCount() {
        if (mReviewList != null)
            return mReviewList.size();
        else
            return 0;
    }

    /************************************ ViewHolder Class **************************************/

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView mAuthorTextView;
        private TextView mContentTextView;

        public ReviewViewHolder(View view) {
            super(view);
            mAuthorTextView = (TextView) view.findViewById(R.id.tv_review_author);
            mContentTextView = (TextView) view.findViewById(R.id.tv_review_content);
        }
    }
}
