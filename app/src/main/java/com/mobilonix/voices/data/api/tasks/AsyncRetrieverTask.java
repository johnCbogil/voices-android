package com.mobilonix.voices.data.api.tasks;

import android.os.AsyncTask;

import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.model.Politico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AsyncRetrieverTask extends AsyncTask<ApiEngine, Void, Void> implements DataRetrievalTask {

    ArrayList<Politico> mPoliticos;
    ApiEngine[] mEngines;

    RetrievalCompletedListener mRetrievalCompletedCallback;

    @Override
    public void loadApiEngines(ApiEngine... engines) {
        mEngines = engines;
    }

    @Override
    public void startRetrieval() {

        mPoliticos = new ArrayList<>();

        if(mEngines == null) {
            throw new RuntimeException("APIEngines cannot be null - check initialization");
        }

        execute(mEngines);
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mRetrievalCompletedCallback.onRetrievalComplete(mPoliticos);
    }

    @Override
    public void cancelRetrieval() {
        cancel(true);
    }

    @Override
    public void setOnRetrievalCompletedListener(RetrievalCompletedListener listener) {
        mRetrievalCompletedCallback = listener;
    }

    @Override
    protected Void doInBackground(ApiEngine... engines) {

        for (ApiEngine engine : engines) {
            retrievePoliticoData(engine);
        }
        return null;
    }

    void retrievePoliticoData(ApiEngine engine) {

        for (Politico p : retrievePoliticos(engine)) {
//            Log.i ("async", retrievePoliticos(engine).toString());
            mPoliticos.add(p);
        }
    }

    private List<Politico> retrievePoliticos(ApiEngine engine) {
        try {
            return engine.retrieveData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
