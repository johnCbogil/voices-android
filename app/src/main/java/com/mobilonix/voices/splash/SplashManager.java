package com.mobilonix.voices.splash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.location.LocationRequestManager;

import com.mobilonix.voices.location.util.LocationUtil;
import com.mobilonix.voices.representatives.RepresentativesManager;


public enum  SplashManager {

    INSTANCE;

    FrameLayout splashContentFrame;

    public boolean splashScreenVisible = false;

    /**
     * Toggle splash screen on and off
     *
     * @param state
     */
    public void toggleSplashScreen(final VoicesMainActivity activity, boolean state) {

        LayoutInflater inflater
                = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        splashContentFrame
                = (FrameLayout)inflater.inflate(R.layout.view_splash_screen, null, false);

        if(state) {

            Button splashGettingStartedButton = (Button)splashContentFrame
                    .findViewById(R.id.splash_getting_started_button);

            splashGettingStartedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    INSTANCE.toggleSplashScreen(activity, false);
                    if (LocationUtil.isGPSEnabled(activity)) {
                        RepresentativesManager.INSTANCE
                                .toggleRepresentativesScreen(
                                        activity.getCurrentLocation(),
                                        activity, true);
                    } else {
                        LocationRequestManager.INSTANCE.toggleLocationRequestScreen(activity, true);
                    }
                }
            });


            splashScreenVisible = true;

            activity.getMainContentFrame().addView(splashContentFrame);
        } else {

            splashScreenVisible = false;

            splashContentFrame.setVisibility(View.GONE);
            activity.getMainContentFrame().removeView(splashContentFrame);
        }
    }

}
