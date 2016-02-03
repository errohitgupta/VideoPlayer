package com.videoplayer.videoplayer.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.videoplayer.videoplayer.exoplayer.player.MediaControllerView;
import com.videoplayer.videoplayer.R;
import com.videoplayer.videoplayer.utils.VideoPlayer;

/**
 * Created by rohit on 27/01/16.
 */
public class PlayerActivity extends AppCompatActivity implements VideoPlayer.PlayerState, MediaControllerView.SetOrientation,MediaControllerView.MediaControllerViewToggler{
    private Toolbar mToolbar;
    private VideoPlayer mVideoPlayer;
    private FrameLayout mShareLayout,mPlayerLayout;
    private LinearLayout mReplayLayout;
    private View mView;
    private static final String TAG = PlayerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate Called");
        setContentView(R.layout.activity_player);
        initLayout();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setUpVideoPlayBack();
    }

    private void initLayout(){
        mToolbar = (Toolbar) findViewById(R.id.transparent_toolbar);
        mVideoPlayer = (VideoPlayer) findViewById(R.id.video_player);
        mShareLayout = (FrameLayout) findViewById(R.id.share_layout);
        mReplayLayout = (LinearLayout) findViewById(R.id.replay_layout);
        mPlayerLayout = (FrameLayout) findViewById(R.id.player_layout);
        mView = findViewById(R.id.view);
    }

    private void setUpVideoPlayBack(){
//        mVideoPlayer.setMediaControls(true);
//        mVideoPlayer.setAspectRatio(VideoPlayer.FILL_ASPECT_RATIO);
        mVideoPlayer.setContentType(VideoPlayer.TYPE_OTHER);
        mVideoPlayer.setContentUri(Uri.parse("http://redirector.c.youtube.com/videoplayback?id=604ed5ce52eda7ee&itag=22&source=youtube&sparams=ip,ipbits,expire,source,id&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=513F28C7FDCBEC60A66C86C9A393556C99DC47FB.04C88036EEE12565A1ED864A875A58F15D8B5300&key=ik0"));
        mVideoPlayer.start();
    }

/*    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("Screen Orientation", "Orientation Changed");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Screen Orientation","Landscape");
            mView.setVisibility(View.GONE);
            mPlayerLayout.setBackgroundColor(000000);
            ViewGroup.LayoutParams params= mPlayerLayout.getLayoutParams();
            params.height= ViewGroup.LayoutParams.MATCH_PARENT;
            params.width= ViewGroup.LayoutParams.MATCH_PARENT;
            mPlayerLayout.setLayoutParams(params);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("Screen Orientation","Potrait");
            mView.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params1= mPlayerLayout.getLayoutParams();
            params1.height= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());;
            mPlayerLayout.setLayoutParams(params1);
        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume Called");
        if(mVideoPlayer!=null){
            setUpVideoPlayBack();
        }
    }

    @Override
    public void getPlayerState(int playerState) {
        Log.d("checkPlayerState", String.valueOf(playerState));

        if(playerState == VideoPlayer.STATE_ENDED){
            mVideoPlayer.stopPlayer();
            setShareLayout();
        }
    }

    public void setShareLayout() {
        mShareLayout.setVisibility(View.VISIBLE);
        mReplayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareLayout.setVisibility(View.GONE);
                setUpVideoPlayBack();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause Called");
        try{
            if(mVideoPlayer!=null){
                mVideoPlayer.stopPlayer();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop Called");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");
        if(mVideoPlayer.isFullScreen()){
            playPortrait();
        }else{
            super.onBackPressed();
        }
    }



    @Override
    public void playPortrait() {
        mView.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params= mPlayerLayout.getLayoutParams();
        params.height= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
        params.width= ViewGroup.LayoutParams.MATCH_PARENT;
        mVideoPlayer.playPortrait(this);
    }

    @Override
    public void playLandscape() {
        mView.setVisibility(View.GONE);
        mPlayerLayout.setBackgroundColor(000000);
        ViewGroup.LayoutParams params= mPlayerLayout.getLayoutParams();
        params.height= ViewGroup.LayoutParams.MATCH_PARENT;
        params.width= ViewGroup.LayoutParams.MATCH_PARENT;
        mPlayerLayout.setLayoutParams(params);
        mVideoPlayer.playLandscape(this);
    }

    @Override
    public void toggleControllerView() {
//        mVideoPlayer.toggleControllerView();
    }
}
