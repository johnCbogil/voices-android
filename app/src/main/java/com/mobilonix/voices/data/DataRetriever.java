package com.mobilonix.voices.data;


import android.util.Log;


import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.tasks.AsyncRetrieverTask;
import com.mobilonix.voices.data.api.tasks.DataRetrievalTask;
import com.mobilonix.voices.data.api.tasks.RetrievalCompletedListener;
import com.mobilonix.voices.data.api.util.HttpRequestor;
import com.mobilonix.voices.data.api.util.UrlConnectionRequestor;
import com.mobilonix.voices.data.model.Politico;

import java.util.ArrayList;

//TODO this now runs ApiEngines Serially - Should run the parallely
//TODO also, which APIs are executed should have some logic based on location (lat,lon)
public class DataRetriever {

    public static final String TAG = "DataRetriever";

    DataRetrievalTask retrieverTask = new AsyncRetrieverTask();
    ApiEngine[] mApiEngines;
    HttpRequestor mRequestor;

    DataRetriever(double lat, double lon, ApiEngine... apiEngines) {

        mRequestor = new UrlConnectionRequestor();

        mApiEngines = apiEngines;
        initEngines(lat, lon, mApiEngines);
    }

    DataRetriever(String address, ApiEngine... apiEngines) {

    }

    private void initEngines(double lat, double lon, ApiEngine[] apiEngines){

        for (ApiEngine engine: apiEngines) {
            engine.initialize(lat,lon, mRequestor);
        }
    }

    public void retrieveData() {

        retrieverTask.setOnRetrievalCompletedListener(new RetrievalCompletedListener() {
            @Override
            public void onRetrievalComplete(ArrayList<Politico> responses) {
                Log.i(TAG, responses.toString());
            }
        });

        retrieverTask.loadApiEngines(mApiEngines);
        retrieverTask.startRetrieval();
    }
}
