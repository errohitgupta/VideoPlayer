package com.videoplayer.videoplayer.utils;

/**
 * Created by rohit on 27/01/16.
 */
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.CaptioningManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.videoplayer.videoplayer.exoplayer.SmoothStreamingTestMediaDrmCallback;
import com.videoplayer.videoplayer.exoplayer.WidevineTestMediaDrmCallback;
import com.videoplayer.videoplayer.exoplayer.player.DashRendererBuilder;
import com.videoplayer.videoplayer.exoplayer.player.DemoPlayer;
import com.videoplayer.videoplayer.exoplayer.player.ExtractorRendererBuilder;
import com.videoplayer.videoplayer.exoplayer.player.HlsRendererBuilder;
import com.videoplayer.videoplayer.exoplayer.player.MediaControllerView;
import com.videoplayer.videoplayer.exoplayer.player.SmoothStreamingRendererBuilder;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;
import com.videoplayer.videoplayer.R;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.Map;

public class VideoPlayer extends FrameLayout implements SurfaceHolder.Callback, DemoPlayer.Listener,
        DemoPlayer.CaptionListener, DemoPlayer.Id3MetadataListener, AudioCapabilitiesReceiver.Listener{

    public static final int TYPE_DASH = 0;
    public static final int TYPE_SS = 1;
    public static final int TYPE_HLS = 2;
    public static final int TYPE_OTHER = 3;
    public static final int FILL_ASPECT_RATIO=0;
    public static final int FILL_WITDH=1;
    public static final int FILL_HEIGHT=2;
    public static final int FILL_LAYOUT=3;
    public static final int STATE_IDLE = 1;
    public static final int STATE_PREPARING = 2;
    public static final int STATE_BUFFERING = 3;
    public static final int STATE_READY = 4;
    public static final int STATE_ENDED = 5;
    public static final int TRACK_DISABLED = -1;
    public static final int TRACK_DEFAULT = 0;
    public static String COOKIE="unspecified";


    private static final CookieManager defaultCookieManager;
    static {
        defaultCookieManager = new CookieManager();
        defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private static VideoPlayer instance;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private static Context __context;
    private DemoPlayer __player;
    private long __playerPosition;
    private boolean __playerNeedsPrepare;
    private Uri __contentUri;
    private int __contentType;
    private boolean __mediaControls=true;
    private String __contentId;
    private View __shutterView;
    private FrameLayout __videoFrame;
    private MediaControllerView mediaController;
    private ProgressBar bufferingBar;
    private Boolean __fullScreen;
    private String cookieId=COOKIE;
    PlayerState mPlayerState;
    private int defaultWidth=0;

    public VideoPlayer(Context context) {
        super(context);
        initVideoPlayerView(context);
    }

    public VideoPlayer(Context context,@Nullable AttributeSet attrs) {
        super(context, attrs);
        initVideoPlayerView(context);
    }

    public VideoPlayer(Context context,@Nullable  AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
        initVideoPlayerView(context);
    }

    public static VideoPlayer getInstance(Context context){
        if (instance != null) {
            return instance;
        } else {
            instance = new VideoPlayer(context);
            return instance;
        }
    }

    private void initVideoPlayerView(Context context){
        __context=context;
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        defaultWidth = point.x;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.videoplayer_view, this);
        __videoFrame  = (FrameLayout) findViewById(R.id.root);
        __shutterView = findViewById(R.id.shutter);
        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(__context,this);
        audioCapabilitiesReceiver.register();
        bufferingBar=(ProgressBar)findViewById(R.id.bufferingBar);
        __fullScreen=false;
    }

    public void setContentType(int type){
        this.__contentType=type;
    }

/*    private void toggleControlsVisibility()  {
        if (mediaController.isShowing()) {
            mediaController.hide();
        } else {
            showControls();
        }
    }*/

/*    private void showControls() {
        mediaController.show(3000);
    }*/

    public int getContentType(){
        return this.__contentType;
    }
/*
    public void setContentId(String contentId){ __contentId=contentId;}
    public String getContentId(){
        return this.__contentId;
    }*/

    public void setContentUri(Uri uri){
        this.__contentUri=uri;
    }
    public Uri getContentUri(){
        return this.__contentUri;
    }

    public void start(){
        this.preparePlayer(true);
    }

    public boolean isFullScreen() {
        return __fullScreen;
    }

/*    public void toggleControllerView() {
        Log.d("MediaController","ToggleView Called");
        toggleControlsVisibility();
    }*/

 /*   public void mediaController(){
        View root = findViewById(R.id.root);
        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
        root.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE
                        || keyCode == KeyEvent.KEYCODE_MENU) {
                    return false;
                }
                return mediaController.dispatchKeyEvent(event);
            }
        });
//        mediaController = new MediaControllerView(__context);
//        mediaController.setAnchorView((ViewGroup) root);
    }*/
/*    public void setMediaControls(boolean __mediaControls) {
        this.__mediaControls = __mediaControls;
    }*/

    public void stopPlayer(){
        if(__player!=null){
//            mediaController.hide();
            __playerNeedsPrepare=true;
            __mediaControls=false;
            releasePlayer();

        }
    }
    @Override
    public void onCues(List<Cue> cues) {

    }

    @Override
    public void onId3Metadata(Map<String, Object> metadata) {

    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
//            showControls();
        }
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
//        mPlayerState.getPlayerState(playbackState);
        switch(playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                bufferingBar.setVisibility(VISIBLE);
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                bufferingBar.setVisibility(GONE);
                __playerPosition = 0;
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                bufferingBar.setVisibility(GONE);
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                bufferingBar.setVisibility(GONE);
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                bufferingBar.setVisibility(GONE);
                break;
            default:
                text += "unknown";
                bufferingBar.setVisibility(GONE);
                break;
        }
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {

    }

    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) __context.getSystemService(Context.CAPTIONING_SERVICE);
        return captioningManager.getFontScale();
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) __context.getSystemService(Context.CAPTIONING_SERVICE);
        return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
    }

    @Override
    public void onError(Exception e) {
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            int stringId = Util.SDK_INT < 18 ? R.string.drm_error_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.drm_error_unsupported_scheme : R.string.drm_error_unknown;
            Toast.makeText(__context, stringId, Toast.LENGTH_LONG).show();
        }
        __playerNeedsPrepare = true;
    }

/*    public float getAspectRatio(int aspectRatioType){
        float aspectRatio= (float) 0.0;
        switch (aspectRatioType){
            case FILL_ASPECT_RATIO:
                aspectRatio=1.0f;
                return aspectRatio;
            case FILL_HEIGHT:
                return aspectRatio;
            case FILL_WITDH:
                return aspectRatio;
            case FILL_LAYOUT:
                return aspectRatio;
        }
        return aspectRatio;
    }

    public void setAspectRatio(int aspectRatioType){
        switch (aspectRatioType){
            case FILL_ASPECT_RATIO:
                __videoFrame.setAspectRatio(getAspectRatio(aspectRatioType));
                break;
            case FILL_HEIGHT:
                __videoFrame.setAspectRatio(getAspectRatio(aspectRatioType));
                break;
            case FILL_WITDH:
                __videoFrame.setAspectRatio(getAspectRatio(aspectRatioType));
                break;
            case FILL_LAYOUT:
                __videoFrame.setAspectRatio(getAspectRatio(aspectRatioType));
                break;
        }
    }*/

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        __shutterView.setVisibility(View.GONE);
        calculateAspectRatio(width,height);
    }

    protected void calculateAspectRatio(int width, int height) {
        int viewWidth = defaultWidth;
        int viewHeight = defaultWidth;

        int videoWidth = width;
        int videoHeight = height;

        float aspect = (float) videoWidth / videoHeight;

        LayoutParams layoutParams = (LayoutParams) getLayoutParams();

        if (((float) viewWidth / videoWidth) > ((float) viewHeight / videoHeight)) {
            layoutParams.width = (int) (viewHeight * aspect + 0.5F);
            layoutParams.height = viewHeight;
        } else {
            layoutParams.width = viewWidth;
            layoutParams.height = (int) (viewWidth / aspect + 0.5F);
        }

        layoutParams.gravity = Gravity.FILL;


        Log.d("20672067", "calculateAspectRatio:" + layoutParams.width + "--" + layoutParams.height);

        setLayoutParams(layoutParams);
    }

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(__context, "VideoPlayerView");
        switch (__contentType) {
            case TYPE_SS:
                return new SmoothStreamingRendererBuilder(__context, userAgent, __contentUri.toString(),
                        new SmoothStreamingTestMediaDrmCallback());
            case TYPE_DASH:
                return new DashRendererBuilder(__context, userAgent, __contentUri.toString(),
                        new WidevineTestMediaDrmCallback(__contentId));
            case TYPE_HLS:
                return new HlsRendererBuilder(__context, userAgent, __contentUri.toString());
            case TYPE_OTHER:
                return new ExtractorRendererBuilder(__context, userAgent, __contentUri);
            default:
                throw new IllegalStateException("Unsupported type: " + __contentType);
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (__player == null) {
            __player = new DemoPlayer(getRendererBuilder());
            __player.addListener(this);
            __player.setCaptionListener(this);
            __player.setMetadataListener(this);
            Log.d("PlayerPosition", __playerPosition + "");
            __player.seekTo(__playerPosition);
            __playerNeedsPrepare = true;
//            this.mediaController();
//            mediaController.setMediaPlayer(__player.getPlayerControl());
//            mediaController.setEnabled(__mediaControls);
        }
        if (__playerNeedsPrepare) {
            __player.prepare();
            __playerNeedsPrepare = false;
        }
        SurfaceView surfaceView=(SurfaceView)findViewById(R.id.surface_view);
        __player.setSurface(surfaceView.getHolder().getSurface());
        __player.setPlayWhenReady(playWhenReady);
    }

    private void releasePlayer() {
        if (__player != null) {
            __playerPosition = __player.getCurrentPosition();
            __player.release();
            __player = null;
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("Screen Orientation", "Orientation Changed");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Screen Orientation", "Landscape");
            MediaControllerView.setmFullScreen(true);
            __fullScreen=true;
//            mediaController.updateFullScreen();
            Log.d("ScreenOrientation",__fullScreen+"");
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("Screen Orientation", "Potrait");

            MediaControllerView.setmFullScreen(false);
            __fullScreen=false;
//            mediaController.updateFullScreen();
            Log.d("ScreenOrientation",__fullScreen+"");
        }
    }

    public void playLandscape(Activity mActivity){
        Log.d("FULLSCREEENTEST", "landscapeCalled");
        MediaControllerView.setmFullScreen(true);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        __fullScreen=true;
    }

    public void playPortrait(Activity mActivity){
        Log.d("FULLSCREEENTEST", "potraitCalled");
        MediaControllerView.setmFullScreen(false);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        __fullScreen=false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (__player != null) {
            __player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (__player != null) {
            __player.blockingClearSurface();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public interface PlayerState{
        void getPlayerState(int playerState);
    }

    public void onRelease() {
        releasePlayer();
        audioCapabilitiesReceiver.unregister();
        instance = null;

    }

}
