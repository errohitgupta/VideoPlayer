package com.videoplayer.videoplayer.utils;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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

        mVideoPlayer = VideoPlayer.getInstance(appContext);

        /*setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartPlayer();
            }
        });*/

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (addedVideo && rowParent != null && rowParent.equals(view)) {
                    removeVideoView(mVideoPlayer);
                    playPosition = -1;
                    mVideoPlayer.setVisibility(INVISIBLE);
                }
            }
        });
    }

    private List<Integer> getViewPositions(){
        int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

        if (endPosition - startPosition > 1) {
            endPosition = startPosition + 1;
        }

        if (startPosition < 0 || endPosition < 0) {
            return null;
        }

        return null;
    }

    //remove the player from the row
    private void removeVideoView(VideoPlayer videoView) {

        ViewGroup parent = (ViewGroup) videoView.getParent();

        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            addedVideo = false;
        }
    }

    public void onStartPlayer(){
        mVideoPlayer.setMediaControls(true);
//            mVideoPlayer.setAspectRatio(VideoPlayer.FILL_ASPECT_RATIO);
        mVideoPlayer.setContentType(VideoPlayer.TYPE_OTHER);
        mVideoPlayer.setContentUri(Uri.parse("http://redirector.c.youtube.com/videoplayback?id=604ed5ce52eda7ee&itag=22&source=youtube&sparams=ip,ipbits,expire,source,id&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=513F28C7FDCBEC60A66C86C9A393556C99DC47FB.04C88036EEE12565A1ED864A875A58F15D8B5300&key=ik0"));
        mVideoPlayer.start();
    }

    public void onPausePlayer() {
        if (mVideoPlayer != null) {
            removeVideoView(mVideoPlayer);
            mVideoPlayer.onRelease();
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
