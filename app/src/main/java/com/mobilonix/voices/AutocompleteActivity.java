package com.mobilonix.voices;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class AutocompleteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autocomplete_activity);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        ImageView backButton = (ImageView)findViewById(R.id.toolbar_previous_2);
        ImageView infoButton = (ImageView)findViewById(R.id.toolbar_info);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog addressDialog;
                addressDialog= new Dialog(AutocompleteActivity.this);
                addressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                addressDialog.setContentView(R.layout.dialog_address);
                Button okButton = (Button) addressDialog.findViewById(R.id.ok_button);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addressDialog.dismiss();
                    }
                });
                addressDialog.show();
            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("address", place.getAddress().toString());
                returnIntent.putExtra("latitude", place.getLatLng().latitude);
                returnIntent.putExtra("longitude", place.getLatLng().longitude);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });


    }
}
