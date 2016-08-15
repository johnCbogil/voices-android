package com.mobilonix.voices.location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.location.util.LocationUtil;
import com.mobilonix.voices.representatives.RepresentativesManager;

public enum LocationRequestManager {

    INSTANCE;

    public String TAG = LocationRequestManager.class.getCanonicalName();

    FrameLayout locationRequestFrame;
    FrameLayout locationEntryFrame;

    boolean locationRequestScreenOn = false;

    /**
     * Toggle location request screen on and off
     *
     * @param state
     */
    public void toggleLocationRequestScreen(final VoicesMainActivity activity, boolean state) {

        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        locationRequestFrame = (FrameLayout)
                inflater.inflate(R.layout.view_location_request_screen, null, false);

        if(state) {

            locationRequestScreenOn = true;

            Button locationRequestButton = (Button)locationRequestFrame
                    .findViewById(R.id.location_request_button);

            locationRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(LocationUtil.isGPSEnabled(activity)) {

                        LocationUtil.triggerLocationUpdate(activity, null);

                        toggleLocationRequestScreen(activity, false);
                        RepresentativesManager.INSTANCE
                                .toggleRepresentativesScreen(
                                        activity.getCurrentLocation(),
                                        activity,
                                        true);
                    } else {
                        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(myIntent);
                    }
                }
            });
            activity.getMainContentFrame().addView(locationRequestFrame);

        } else {

            locationRequestScreenOn = false;
            locationRequestFrame.setVisibility(View.GONE);
            activity.getMainContentFrame().removeView(locationRequestFrame);
        }
    }


    public boolean isLocationRequestScreenOn() {
        return locationRequestScreenOn;
    }

    /**
     * Show the enable GPS Button
     *
     * @param activity
     */
    public void showGPSNotEnabledDialog(VoicesMainActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Location Not Enabled!");
        builder.setMessage("Location is not enabled.  You won't be able to get accurate location requests until this is done.  Please go to your device settings and enable 'Location");
        builder.setPositiveButton("Alright", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        Dialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Show the enable GPS Button
     *
     * @param activity
     */
    public void showGPSEnabledDialog(VoicesMainActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Location Enabled!");
        builder.setMessage("Your device's location detection is now enabled!  You'll be able to get representatives from your current location.");
        builder.setPositiveButton("Great!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        Dialog dialog = builder.create();
        dialog.show();
    }

}
