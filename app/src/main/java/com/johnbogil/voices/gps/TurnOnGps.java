package com.johnbogil.voices.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnbogil.voices.R;
import com.johnbogil.voices.activities.FragmentListActivity;

public class TurnOnGps extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private Button turn_on_gps_button;
    @SuppressWarnings("FieldCanBeLocal")
    private TextView not_right_now;
    private static final String PREFERENCES_FILE = "productions.darkmatter.voices.preferences";
    private static String KEY = "KEY";
    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public String key_value = "0";
    ImageView turn_on_gps_image;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int INITIAL_REQUEST=1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Log.d("ACTIV$$", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_on_gps);
        /*

        turn_on_gps_image = (ImageView) findViewById(R.id.turnOnGpsImage);
        mSharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
     //   Picasso.with(getApplicationContext()).load(R.drawable.onboarding_page_2).into(turn_on_gps_image);
        if (key_value.equals("1")){
            startActivity(new Intent(getApplicationContext(), FragmentListActivity.class));
        }
        not_right_now = (TextView) findViewById(R.id.not_now_gps_button);
        not_right_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FragmentListActivity.class));
            }
        });

        */

        not_right_now = (TextView) findViewById(R.id.not_now_gps_button);
        not_right_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FragmentListActivity.class));
            }
        });


        mSharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        turn_on_gps_button = (Button) findViewById(R.id.turn_on_gps_button);
        turn_on_gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key_value = "1";
                mEditor.putString(KEY, key_value);
                mEditor.apply();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
    }

    @Override
    protected void onRestart() {

        Log.d("ACTIV$$", "onRestart");

        super.onRestart();
        Tracker track = new Tracker(this);
        if (track.canGetLocation && key_value.equals("1")){
            startActivity(new Intent(getApplicationContext(), FragmentListActivity.class));
        }
    }
}
