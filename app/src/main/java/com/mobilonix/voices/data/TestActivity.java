package com.mobilonix.voices.data;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mobilonix.voices.R;
import com.mobilonix.voices.data.api.engines.CongressSunlightApi;
import com.mobilonix.voices.data.api.engines.OpenStatesApi;

import org.json.JSONException;
import org.json.JSONObject;

public class TestActivity extends AppCompatActivity {

    DataRetriever retriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.api_test);

        testApiCrawler();
    }

    void testApiCrawler() {

        CongressSunlightApi sunlightApi = new CongressSunlightApi();
        OpenStatesApi api = new OpenStatesApi();

        double lat = 40.758896, lon = -73.985130;

        retriever = new DataRetriever(lat, lon, sunlightApi, api);
        retriever.retrieveData();
    }

    private JSONObject stringToJson(String string) {

        try {
            return new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace(); //TODO handle exception
        }

        return null;
    }
}
