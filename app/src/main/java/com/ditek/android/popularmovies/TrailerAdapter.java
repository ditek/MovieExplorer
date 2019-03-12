package com.ditek.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ditek.android.popularmovies.utilities.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diaa on 3/30/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Trailer> mTrailerList;
    final private ItemClickListener mOnClickListener;

    TrailerAdapter(final Context context) {
        mOnClickListener = new ItemClickListener() {
            @Override
            public void onListItemClick(int clickedItemIndex) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utilities.buildYoutubeVideoUrl(mTrailerList.get(clickedItemIndex).key).toString()));
                if (intent.resolveActivity(context.getPackageManager()) != null)
                    context.startActivity(intent);
            }
        };
    }

    public interface ItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public void setData(List<Trailer> data) {
        mTrailerList = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        if (mTrailerList != null && mTrailerList.size() > position) {
            Picasso.with(holder.mImageView.getContext())
                    .load(mTrailerList.get(position).getThumbnailPath())
                    .into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mTrailerList != null)
            return mTrailerList.size();
        else
            return 0;
    }

    /************************************ ViewHolder Class **************************************/

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;

        public TrailerViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.iv_trailer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
