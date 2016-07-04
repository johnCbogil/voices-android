package com.mobilonix.voices.data;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.data.api.engines.CongressSunlightApi;
import com.mobilonix.voices.data.api.engines.OpenStatesApi;
import com.mobilonix.voices.data.api.util.GpsLocationManager;


public class TestActivity extends AppCompatActivity {

    public static final String TAG = "TestActivity";

    public static final int COARSE_LOCATION_REQUEST_ID = 1500;
    public static final int FINE_LOCATION_REQUEST_ID = 1501;

    DataRetriever mRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.api_test);

        Log.i(TAG, "HELLO before permission check");


        if(doesHavePermissions()) {

            Log.i(TAG, "HAS PERMISSIONS");
            testApiCrawler();
        } else {
            permRequest();
            Log.i(TAG, "NO PERMISSIONS");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.i(TAG, "before permission check");

        testApiCrawler();
    }

    void testApiCrawler() {

        if(doesHavePermissions()) {

            GpsLocationManager locationManager = new GpsLocationManager(this);

            double lat = locationManager.getLatitude();
            double lon = locationManager.getLongitude();

            Log.i(TAG, "has permissions");

            CongressSunlightApi sunlightApi = new CongressSunlightApi(getString(R.string.sunlight_api_key));
            OpenStatesApi api = new OpenStatesApi(getString(R.string.open_states_api_key));

            mRetriever = new DataRetriever(lat, lon, sunlightApi, api);
            mRetriever.retrieveData();
        } else {

            TextView v = (TextView) findViewById(R.id.test_textview);

            v.setText("insufficient permissions to run app, see logcat");

        }
    }

    // --------------------------------- Permission Requesting ------------------------------------

    public void permRequest() {

        Log.i(TAG, "IN PERM REQUEST");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "IN FINE LOCATION");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_REQUEST_ID);
        }

        if ((ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            Log.i(TAG, "IN COARSE LOCATION");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    COARSE_LOCATION_REQUEST_ID);
        }
    }

    public boolean doesHavePermissions() {

        boolean isFineLocationEnabled = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        boolean isCoarseLocationEnabled = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        Log.i(TAG, "doesHavePermission = " + (isCoarseLocationEnabled && isFineLocationEnabled));

        return isFineLocationEnabled && isCoarseLocationEnabled;
    }
}
