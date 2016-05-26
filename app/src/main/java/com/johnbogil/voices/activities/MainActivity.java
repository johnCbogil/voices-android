package com.johnbogil.voices.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.johnbogil.voices.AnalyticsApplication;
import com.johnbogil.voices.R;
import com.johnbogil.voices.gps.Tracker;
import com.johnbogil.voices.gps.TurnOnGps;

public class MainActivity extends Activity {

    private static final String PREFERENCES_FILE = "productions.darkmatter.voices.preferences";
    private static String KEY = "KEY";
    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    @SuppressWarnings("FieldCanBeLocal")
    private Button get_started_button;
    public String key_value;

    Tracker tracker;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            mSharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();

            tracker = new Tracker(this);

            key_value = mSharedPreferences.getString(KEY + "", "");

            if (key_value.equals("1")) {

//                if (tracker.canGetLocation()) {
//
//                    startActivity(new Intent(getApplicationContext(), TurnOnGps.class));
//
//                } else {
//                    requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
//                }
//
                startActivity(new Intent(getApplicationContext(), FragmentListActivity.class));

                return;
            }

            get_started_button = (Button) findViewById(R.id.get_started_button);
            get_started_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    key_value = "1";
                    mEditor.putString(KEY, key_value);
                    mEditor.apply();
                    Intent intent = new Intent(getApplicationContext(), TurnOnGps.class);
                    startActivity(intent);
                }
            });

        } catch(Exception e){
            e.printStackTrace();
           Toast.makeText(MainActivity.this, String.valueOf(e),Toast.LENGTH_LONG).show();
        }



    }

    /*
        private static final String[] INITIAL_PERMS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
        };
        private static final int INITIAL_REQUEST = 1337;
*/

    //            if (tracker.canGetLocation()) {
//                //DEBUG Log.d("$$$ MaAct location ", Double.toString(tracker.getLatitude()));
//                //DEBUG Log.d("$$$ MaAct location ", Double.toString(tracker.getLongitude()));
//                // requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);  only for 6.0,  don't uncomment
//
//            } else {
//                //DEBUG Log.d("$$$ MaActivity location", "cant get location");
//            }

    //          Crittercism.initialize(getApplicationContext(), "63a40cb73ad544569fd515d665223dcc00555300");


}