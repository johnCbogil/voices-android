package com.mobilonix.voices.location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.location.model.LatLong;
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
                        toggleLocationEntryScreen(activity, true);
                    } else {
                        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(myIntent);
                    }
                }
            });
            activity.getMainContentFrame().addView(locationRequestFrame);

        } else {

            locationRequestScreenOn = false;
            activity.getMainContentFrame().removeView(locationRequestFrame);
        }
    }

    public void toggleLocationEntryScreen(final VoicesMainActivity activity, boolean state) {
        if(state) {
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try {
                locationEntryFrame = (FrameLayout) inflater.inflate(R.layout.view_location_entry, null, false);
            } catch (Exception e) {}
            Button findByLocationButton = (Button)locationEntryFrame.findViewById(R.id.find_for_current_location_button);
            Button findByEntryButton = (Button)locationEntryFrame.findViewById(R.id.find_for_entered_location_button);

            try {
                final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        activity.getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        Log.e(TAG, "It worked!");
                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.e(TAG, "An error occurred: " + status);

                    }
                });
            } catch (Exception e) {}

            findByEntryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(!LocationUtil.isGPSEnabled(activity)) {
                        LocationRequestManager.INSTANCE.showGPSNotEnabledDialog(activity);
                    } else {}

                    RepresentativesManager.INSTANCE
                                .toggleRepresentativesScreen(
                                        activity.getCurrentLocation(),
                                        activity, true);
                    toggleLocationEntryScreen(activity, false);

                }
            });

            findByLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LatLong location = activity.getCurrentLocation();
                    //FIXME unhardcode below
//                    location = new LatLong(40.7282, -74.0776);

                    if(!LocationUtil.isGPSEnabled(activity)) {
                        LocationRequestManager.INSTANCE.showGPSNotEnabledDialog(activity);
                    } else {}

                    RepresentativesManager.INSTANCE
                            .toggleRepresentativesScreen(
                                    location,
                                    activity, true);
                    toggleLocationEntryScreen(activity, false);

                }
            });

            activity.getMainContentFrame().addView(locationEntryFrame);
        } else {
            activity.getMainContentFrame().removeView(locationEntryFrame);
        }
    }

    public boolean isLocationRequestScreenOn() {
        return locationRequestScreenOn;
    }

    /**
     * This method takes in a location and returns a lat long.  The location can come from some form
     * of user Input
     *
     * @param location
     * @return
     */
    public LatLong getLatLongFromLocation(String location) {
        return new LatLong(0, 0);
    }

    /**
     * Show the enable GPS Button
     *
     * @param activity
     */
    public void showGPSNotEnabledDialog(VoicesMainActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Location Not Enabled!");
        builder.setMessage("Location is not enabled.  You won't be able get accurate location requests until this is done.  Please goto your device settings and enable 'Location");
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
        builder.setMessage("Your device's Location detection is now enabled!  You'll be able to get representatives from your current location");
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
