package com.videoplayer.videoplayer.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.videoplayer.videoplayer.R;

/**
 * Created by rohit on 27/01/16.
 */
public class AlertBox {

    public static void closeApplication(final Activity mActivity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getResources().getString(R.string.exit_box_title));
        builder.setCancelable(false);
        builder.setMessage(mActivity.getResources().getString(R.string.exit_box_message));
        builder.setPositiveButton(mActivity.getResources().getString(R.string.exit_box_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mActivity.finish();
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder.setNegativeButton(mActivity.getResources().getString(R.string.exit_box_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
