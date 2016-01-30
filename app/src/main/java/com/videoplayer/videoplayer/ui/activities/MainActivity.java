package com.videoplayer.videoplayer.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.videoplayer.videoplayer.R;
import com.videoplayer.videoplayer.Utils.AlertBox;
import com.videoplayer.videoplayer.ui.adapters.VideoListAdapter;

/**
 * Created by rohit on 27/01/16.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private RecyclerView mVideoListView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private RecyclerView.Adapter mVideoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate Called");
        setContentView(R.layout.activity_main);
        initLayout();
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setupVideoListView();
    }

    private void initLayout(){
        mVideoListView = (RecyclerView)findViewById(R.id.video_list_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setupVideoListView(){
        mVideoListView.setHasFixedSize(true);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mVideoListView.setLayoutManager(mStaggeredLayoutManager);
        mVideoListAdapter = new VideoListAdapter(this);
        mVideoListView.setAdapter(mVideoListAdapter);
        mVideoListView.setNestedScrollingEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume Called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop Called");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");
        AlertBox.closeApplication(this);
    }
}
