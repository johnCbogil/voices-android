package com.mobilonix.voices.splash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.callbacks.Callback;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.util.GeneralUtil;


public enum  SplashManager {
    //Toggle splash screen on and off
    INSTANCE;
    FrameLayout splashContentFrame;
    public boolean splashScreenVisible = false;

    public void toggleSplashScreen(final VoicesMainActivity activity, boolean state, final Callback<Boolean> result) {
        //TODO: This messes up the group tab, need to find out why
        if(splashContentFrame == null) {
            LayoutInflater inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            splashContentFrame = (FrameLayout) inflater.inflate(R.layout.view_splash_screen, null, false);
        }
        if(state) {
            Button splashGettingStartedButton = (Button)splashContentFrame
                    .findViewById(R.id.splash_getting_started_button);
            splashGettingStartedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    INSTANCE.toggleSplashScreen(activity, false, new Callback<Boolean>() {
                        @Override
                        public boolean onExecuted(Boolean data) {
                            RepresentativesManager.INSTANCE
                                    .toggleRepresentativesScreen(null, activity, true);
                            return false;
                        }
                    });
                }
            });
            splashScreenVisible = true;
            ViewGroup parent =(ViewGroup)splashContentFrame.getParent();
            if(parent != null)parent.removeView(splashContentFrame);
            activity.getMainContentFrame().addView(splashContentFrame);
        } else {
            final Animation animationFadeOut
                    = AnimationUtils.loadAnimation(splashContentFrame.getContext(),
                    R.anim.anim_fade_out);
            animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    splashScreenVisible = false;
                    splashContentFrame.setVisibility(View.GONE);
                    activity.getMainContentFrame().removeView(splashContentFrame);
                    if(result != null) {
                        result.onExecuted(true);
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            splashContentFrame.startAnimation(animationFadeOut);
        }
    }

    public boolean toggleOnBoardingCopy(boolean status) {
        if(!status) {
            splashContentFrame.findViewById(R.id.splash_getting_started_button).setVisibility(View.GONE);
            splashContentFrame.findViewById(R.id.splash_voices_intro_text).setVisibility(View.GONE);

            View view = splashContentFrame.findViewById(R.id.splash_voices_image);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            splashContentFrame.findViewById(R.id.splash_voices_image).setLayoutParams(params);
        }
        return status;
    }
}