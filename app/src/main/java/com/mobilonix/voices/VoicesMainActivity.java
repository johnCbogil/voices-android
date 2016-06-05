package com.mobilonix.voices;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.mobilonix.voices.base.util.GeneralUtil;

public class VoicesMainActivity extends AppCompatActivity {


    FrameLayout mainContentFrame;

    /* Views Different Actions */
    FrameLayout splashContentFrame;
    FrameLayout locationRequestFrame;

    Button splashGettingStartedButton;
    Button locationRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voices_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initViews();

        toggleSplashScreen(true);

    }

    private void initViews() {
        mainContentFrame = (FrameLayout)findViewById(R.id.main_content_frame);
    }

    /**
     * Toggle splash screen on and off
     *
     * @param state
     */
    public void toggleSplashScreen(boolean state) {
        if(state) {

            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            splashContentFrame = (FrameLayout)inflater.inflate(R.layout.view_splash_screen, null, false);
            splashGettingStartedButton = (Button)splashContentFrame
                    .findViewById(R.id.splash_getting_started_button);

            splashGettingStartedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSplashScreen(false);
                    if(GeneralUtil.isGPSEnabled(VoicesMainActivity.this)) {
                        GeneralUtil.toast(VoicesMainActivity.this, "Location services already enabled");
                    } else {
                        GeneralUtil.toast(VoicesMainActivity.this, "Location services not enabled...");
                        toggleLocationRequestScreen(true);
                    }
                }
            });

            mainContentFrame.addView(splashContentFrame);
        } else {
            mainContentFrame.removeView(splashContentFrame);
        }
    }

    /**
     * Toggle location request screen on and off
     *
     * @param state
     */
    public void toggleLocationRequestScreen(boolean state) {
        if(state) {
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            locationRequestFrame = (FrameLayout)
                    inflater.inflate(R.layout.view_location_request_screen, null, false);
            locationRequestButton = (Button)locationRequestFrame
                    .findViewById(R.id.location_request_button);

            locationRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(GeneralUtil.isGPSEnabled(VoicesMainActivity.this)) {
                        GeneralUtil.toast(VoicesMainActivity.this, "Location services already enabled");
                        toggleLocationRequestScreen(false);
                    } else {
                        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                }
            });
            mainContentFrame.addView(locationRequestFrame);

        } else {
            mainContentFrame.removeView(locationRequestFrame);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
}
