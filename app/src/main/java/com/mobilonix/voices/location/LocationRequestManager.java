package com.mobilonix.voices.location;

import android.content.Context;
import android.content.Intent;
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

        LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(state) {

            locationEntryFrame = (FrameLayout)
                    inflater.inflate(R.layout.view_location_entry, null, false);

            final ListView autoCompleteLocationList = (ListView)locationEntryFrame.findViewById(R.id.autocomplete_location_list);
            EditText locationEntryField = (EditText)locationEntryFrame.findViewById(R.id.location_entry_field);
            Button findByLocationButton = (Button)locationEntryFrame.findViewById(R.id.find_for_current_location_button);
            Button findByEntryButton = (Button)locationEntryFrame.findViewById(R.id.find_for_current_location_button);

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
                            .toggleRepresentativesScreen(activity, true);
                    toggleLocationEntryScreen(activity, false);
                }
            });

            findByLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RepresentativesManager.INSTANCE
                            .toggleRepresentativesScreen(activity, true);
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
}
