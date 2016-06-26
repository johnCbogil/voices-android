package com.mobilonix.voices;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.badoo.mobile.util.WeakHandler;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.location.LocationRequestManager;
import com.mobilonix.voices.splash.SplashManager;
import com.mobilonix.voices.util.ViewUtil;

public class VoicesMainActivity extends AppCompatActivity {

    FrameLayout mainContentFrame;
    boolean leaveAppDialogShowing = false;
    WeakHandler handler = new WeakHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voices_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initViews();

        SplashManager.INSTANCE.toggleSplashScreen(this, true);

    }

    private void initViews() {
        mainContentFrame = (FrameLayout)findViewById(R.id.main_content_frame);
    }

    public FrameLayout getMainContentFrame() {
        return mainContentFrame;
    }

    public WeakHandler getHandler() {

        if (handler == null) {
            handler = new WeakHandler();
        }

        return handler;
    }

    @Override
    public void onBackPressed() {
        if(!leaveAppDialogShowing) {
            showLeaveAppDialog();
        }
    }

    public void showLeaveAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Voices");
        builder.setMessage("Are you sure you want to leave the voices app?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveAppDialogShowing = false;
                        dialog.dismiss();
                        finish();
                    }
                }
        );
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveAppDialogShowing = false;
                        dialog.dismiss();
                    }
                }
        );
        Dialog dialog = builder.create();
        leaveAppDialogShowing = true;
        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(LocationRequestManager.INSTANCE.isLocationRequestScreenOn()) {
            if(GeneralUtil.isGPSEnabled(this)) {
                LocationRequestManager.INSTANCE.toggleLocationRequestScreen(this, false);
                LocationRequestManager.INSTANCE.toggleLocationEntryScreen(this, true);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LocationRequestManager.INSTANCE.toggleLocationRequestScreen(this, false);
        LocationRequestManager.INSTANCE.toggleLocationEntryScreen(this, true);

    }


}
