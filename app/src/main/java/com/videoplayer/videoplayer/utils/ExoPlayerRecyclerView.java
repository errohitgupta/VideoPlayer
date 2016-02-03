package com.videoplayer.videoplayer.utils;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;

import com.videoplayer.videoplayer.ui.adapters.VideoListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ppadmin on 03/02/16.
 */
public class ExoPlayerRecyclerView extends RecyclerView{
    private Context appContext;
    private VideoPlayer mVideoPlayer;
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private int playPosition = -1;
    private boolean addedVideo = false;
    private View rowParent;
    List<VideoListAdapter.VideoViewHolder> videoViewHolderList;

    public ExoPlayerRecyclerView(Context context) {
        super(context);
        initialize(context);
    }

    public ExoPlayerRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ExoPlayerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    public void initialize(Context context){
        appContext = context.getApplicationContext();
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;
        /*setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartPlayer();
            }
        });*/

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    setViewHolders();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (addedVideo && rowParent != null && rowParent.equals(view)) {
                    removeVideoView(mVideoPlayer);
                    mVideoPlayer.setVisibility(GONE);
                }
            }
        });
    }

    private List<Integer> getViewPositions(){
        List<Integer> viewPositions = new ArrayList<>();
        int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

        if (startPosition < 0 || endPosition < 0) {
            return null;
        }
        Log.d("ExoPlayerRecyclerView", "startPosition: " + startPosition);
        Log.d("ExoPlayerRecyclerView", "endPosition: " + endPosition);
        for(int i=startPosition;i<=endPosition;i++)
        {viewPositions.add(i);}
        return viewPositions;
    }
    private void setUpVideoPlayer(){
//        mVideoPlayer.setMediaControls(true);
//            mVideoPlayer.setAspectRatio(VideoPlayer.FILL_ASPECT_RATIO);
        mVideoPlayer.setContentType(VideoPlayer.TYPE_OTHER);
        mVideoPlayer.setContentUri(Uri.parse("http://redirector.c.youtube.com/videoplayback?id=604ed5ce52eda7ee&itag=22&source=youtube&sparams=ip,ipbits,expire,source,id&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=513F28C7FDCBEC60A66C86C9A393556C99DC47FB.04C88036EEE12565A1ED864A875A58F15D8B5300&key=ik0"));
        mVideoPlayer.start();
    }

    private void setClickListener(){
        final int[] playerPosition = new int[1];
        playerPosition[0]=-1;
        for(int i=0;i<videoViewHolderList.size();i++){
            VideoListAdapter.VideoViewHolder holder = videoViewHolderList.get(i);
            if (holder == null) {
                continue;
            }
            final VideoListAdapter.VideoViewHolder finalHolder = holder;
            holder.mVideoImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ExoPlayerRecyclerView","Click Previous "+ playerPosition[0]);
                    Log.d("ExoPlayerRecyclerView","Click Next "+ finalHolder.getAdapterPosition());
                    /*if(rowParent!=null){
                        removeVideoView(mVideoPlayer);
                    }*/
                    if(rowParent!=null && (playerPosition[0]==finalHolder.getAdapterPosition())){
                        //do nothing
                    }
                    else if(rowParent!=null && (playerPosition[0]!=finalHolder.getAdapterPosition())){
                        removeVideoView(mVideoPlayer);
                        playerPosition[0]=finalHolder.getAdapterPosition();
                        mVideoPlayer = VideoPlayer.getInstance(appContext);
                        finalHolder.mVideoImageLayout.addView(mVideoPlayer);
                        addedVideo = true;
                        rowParent = finalHolder.parent;
                        setUpVideoPlayer();

                    }
                    else if (rowParent == null){
                        playerPosition[0] = finalHolder.getAdapterPosition();
                        mVideoPlayer = VideoPlayer.getInstance(appContext);
                        finalHolder.mVideoImageLayout.addView(mVideoPlayer);
                        addedVideo = true;
                        rowParent = finalHolder.parent;
                        setUpVideoPlayer();
                    }
                }
            });
        }
    }

    private void setViewHolders(){
        videoViewHolderList = new ArrayList<>();
        List<Integer> positions = getViewPositions();
        for(int i=0; i<positions.size();i++){
            View child = getChildAt(positions.get(i));
            if(child==null){
                continue;
            }
            videoViewHolderList.add((VideoListAdapter.VideoViewHolder)child.getTag());
        }
        setClickListener();
    }

    //remove the player from the row
    private void removeVideoView(VideoPlayer videoView) {

        ViewGroup parent = (ViewGroup) videoView.getParent();

        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            mVideoPlayer.onRelease();
            parent.removeViewAt(index);
            addedVideo = false;
            rowParent=null;
        }
    }

    public void onPausePlayer() {
        if (mVideoPlayer != null) {
            removeVideoView(mVideoPlayer);
            mVideoPlayer = null;
        }
    }
/*
    public void onRestartPlayer() {
        if (mVideoPlayer == null) {
            playPosition = -1;
            mVideoPlayer = VideoPlayer.getInstance(appContext);
        }
    }*/

    /**
     * release memory
     */
    public void onRelease() {

        if (mVideoPlayer != null) {
            mVideoPlayer.onRelease();
            mVideoPlayer = null;
        }
        rowParent = null;
    }
}
