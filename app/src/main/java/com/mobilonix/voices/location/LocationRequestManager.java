package com.mobilonix.voices.location;

import android.content.Context;
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
                    if(GeneralUtil.isGPSEnabled(activity)) {
                        GeneralUtil.toast("Location services already enabled");
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
            locationEntryFrame = (FrameLayout)inflater.inflate(R.layout.view_location_entry, null, false);

            Button findByLocationButton = (Button)locationEntryFrame.findViewById(R.id.find_for_current_location_button);
            Button findByEntryButton = (Button)locationEntryFrame.findViewById(R.id.find_for_current_location_button);

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

            findByEntryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            findByLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.findViewById(R.id.place_autocomplete_fragment).setVisibility(View.VISIBLE);
                }

            });

            activity.mainContentFrame.addView(locationEntryFrame);
        } else {
            activity.mainContentFrame.removeView(locationEntryFrame);
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

}
