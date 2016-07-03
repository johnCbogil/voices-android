package com.mobilonix.voices.data.api.tasks;



import com.mobilonix.voices.data.api.ApiEngine;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cakiadeg on 6/28/16.
 */
public interface DataRetrievalTask {

    void loadApiEngines(ApiEngine... engines);
    void startRetrieval();
    void cancelRetrieval();
    void setOnRetrievalCompletedListener(RetrievalCompletedListener listener);
}


