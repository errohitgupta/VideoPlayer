package com.videoplayer.videoplayer.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.videoplayer.videoplayer.R;
import com.videoplayer.videoplayer.Utils.Starter;

/**
 * Created by rohit on 27/01/16.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> implements View.OnClickListener {
     private Context mContext;

    public VideoListAdapter(Context context) {
        super();
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate((R.layout.video_feed_layout)
                        , parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mVideoImageView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    @Override
    public void onClick(View v) {
        Starter.startPlayerActivity(mContext);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CardView mFeedCardView;
        public ImageView mVideoImageView;
        public LinearLayout mVideoImageLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            mFeedCardView = (CardView) itemView.findViewById(R.id.media_feed_card);
            mVideoImageView = (ImageView) itemView.findViewById(R.id.media_feed_thumbnail);
            mVideoImageLayout = (LinearLayout) itemView.findViewById(R.id.thumbnail_layout);
        }
    }
}
