package com.videoplayer.videoplayer.Utils;

import android.content.Context;
import android.content.Intent;

import com.videoplayer.videoplayer.ui.activities.PlayerActivity;

/**
 * Created by rohit on 27/01/16.
 */
public class Starter {

    public static void startPlayerActivity(Context context){
        Intent mActivityIntent = new Intent(context,PlayerActivity.class);
        context.startActivity(mActivityIntent);
    }
}
