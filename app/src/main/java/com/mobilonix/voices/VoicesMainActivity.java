package com.mobilonix.voices;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.mobilonix.voices.base.util.GeneralUtil;

public class VoicesMainActivity extends AppCompatActivity {

    public String TAG = VoicesMainActivity.class.getCanonicalName();

    FrameLayout mainContentFrame;

    /* Views Different Actions */
    FrameLayout splashContentFrame;
    FrameLayout locationRequestFrame;
    FrameLayout representativesFrame;
    FrameLayout locationEntryFrame;


    Button splashGettingStartedButton;
    Button locationRequestButton;

    WeakHandler handler = new WeakHandler();

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
                        toggleLocationEntryScreen(true);
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
                        toggleLocationEntryScreen(true);
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

    public void toggleLocationEntryScreen(boolean state) {
        if(state) {
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            locationEntryFrame = (FrameLayout)
                    inflater.inflate(R.layout.view_location_entry, null, false);

            //final ListView autoCompleteLocationList = (ListView)locationEntryFrame.findViewById(R.id.autocomplete_location_list);
            //EditText locationEntryField = (EditText)locationEntryFrame.findViewById(R.id.location_entry_field);
            Button findByLocationButton = (Button)locationEntryFrame.findViewById(R.id.find_for_current_location_button);
            Button findByEntryButton = (Button)locationEntryFrame.findViewById(R.id.find_for_current_location_button);

            final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            findViewById(R.id.place_autocomplete_fragment).setVisibility(View.GONE);

            //filter returns only results with a precise address.

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            autocompleteFragment.setFilter(typeFilter);

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName());//get place details here
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });

            /**
            locationEntryField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    RESTUtil.makeAutoCompleteRequest(new Callback<ArrayList<String>>() {
                        @Override
                        public boolean onExecuted(final ArrayList<String> data) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(autoCompleteLocationList != null) {
                                        autoCompleteLocationList
                                                .setAdapter(new ArrayAdapter(VoicesMainActivity.this,
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
            **/

            findByEntryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            findByLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.place_autocomplete_fragment).setVisibility(View.VISIBLE);
                }

            });

            mainContentFrame.addView(locationEntryFrame);
        } else {
            mainContentFrame.removeView(locationEntryFrame);
        }
    }


    public void toggleRepresentativesScreen(boolean state) {
        if(state) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            representativesFrame = (FrameLayout)
                    inflater.inflate(R.layout.view_representatives, null, false);
            mainContentFrame.addView(representativesFrame);
        } else {
            mainContentFrame.removeView(representativesFrame);
        }
    }

    private void addRssFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        RssFragment fragment = new RssFragment();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        toggleLocationRequestScreen(false);
        toggleLocationEntryScreen(true);

    }
}
