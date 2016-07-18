package com.mobilonix.voices.location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.location.model.LatLong;
import com.mobilonix.voices.location.util.LocationUtil;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.util.RESTUtil;

import java.util.ArrayList;

public enum LocationRequestManager {

    INSTANCE;

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
                        GeneralUtil.toast("Location services already enabled");

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

        LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(state) {

            locationEntryFrame = (FrameLayout)
                    inflater.inflate(R.layout.view_location_entry, null, false);

            final ListView autoCompleteLocationList = (ListView)locationEntryFrame.findViewById(R.id.autocomplete_location_list);
            final EditText locationEntryField = (EditText)locationEntryFrame.findViewById(R.id.location_entry_field);
            Button findByLocationButton = (Button)locationEntryFrame.findViewById(R.id.find_for_current_location_button);
            Button findByEntryButton = (Button)locationEntryFrame.findViewById(R.id.find_for_entered_location_button);

            locationEntryField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    RESTUtil.makeAutoCompleteRequest(new Callback<ArrayList<String>>() {
                        @Override
                        public boolean onExecuted(final ArrayList<String> data) {
                            activity.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (autoCompleteLocationList != null) {
                                        autoCompleteLocationList
                                                .setAdapter(new ArrayAdapter(activity,
                                                        android.R.layout.simple_list_item_1, data));
                                    }
                                }
                            });
                            return true;
                        }
                    });
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            findByEntryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RepresentativesManager.INSTANCE
                            .toggleRepresentativesScreen(
                                    getLatLongFromLocation(locationEntryField.getText().toString()),
                                    activity, true);
                    toggleLocationEntryScreen(activity, false);
                }
            });

            findByLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!LocationUtil.isGPSEnabled(activity)) {
                        GeneralUtil.toast("LOcationn IS NOT ENABLED!");
                        LocationRequestManager.INSTANCE.showGPSNotEnabledDialog(activity);
                    } else {
                        GeneralUtil.toast("LOcationn IS ENABLED!");
                    }

                    RepresentativesManager.INSTANCE
                                .toggleRepresentativesScreen(
                                        activity.getCurrentLocation(),
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
