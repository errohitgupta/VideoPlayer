package com.videoplayer.videoplayer.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.videoplayer.videoplayer.R;
import com.videoplayer.videoplayer.utils.VideoPlayer;

/**
 * Created by rohit on 27/01/16.
 */
public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;

    public VideoListAdapter(Context context) {
        super();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_feed_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VideoViewHolder){
            ((VideoViewHolder)holder).mVideoImageView.setOnClickListener(((VideoViewHolder)holder).imageClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        public CardView mFeedCardView;
        public ImageView mVideoImageView;
        public FrameLayout mVideoImageLayout,mVideoPlayerLayout;
        public VideoPlayer mVideoPlayer;
        private int position;
        public VideoViewHolder(View itemView) {
            super(itemView);
            mFeedCardView = (CardView) itemView.findViewById(R.id.media_feed_card);
            mVideoImageView = (ImageView) itemView.findViewById(R.id.media_feed_thumbnail);
            mVideoImageLayout = (FrameLayout) itemView.findViewById(R.id.thumbnail_layout);
            mVideoPlayerLayout = (FrameLayout) itemView.findViewById(R.id.video_player_layout);
            mVideoPlayer = (VideoPlayer) itemView.findViewById(R.id.video_player);
        }

        private void setUpVideoPlayer(){
            mVideoPlayer.setMediaControls(true);
//            mVideoPlayer.setAspectRatio(VideoPlayer.FILL_ASPECT_RATIO);
            mVideoPlayer.setContentType(VideoPlayer.TYPE_OTHER);
            mVideoPlayer.setContentUri(Uri.parse("http://redirector.c.youtube.com/videoplayback?id=604ed5ce52eda7ee&itag=22&source=youtube&sparams=ip,ipbits,expire,source,id&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=513F28C7FDCBEC60A66C86C9A393556C99DC47FB.04C88036EEE12565A1ED864A875A58F15D8B5300&key=ik0"));
            mVideoPlayer.start();
        }
        public View.OnClickListener imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoImageLayout.setVisibility(View.GONE);
                mVideoPlayerLayout.setVisibility(View.VISIBLE);
                setUpVideoPlayer();
            }
        };
    }
}
