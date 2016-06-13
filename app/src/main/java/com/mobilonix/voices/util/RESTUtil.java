package com.mobilonix.voices.util;

import android.util.Log;

import com.mobilonix.voices.delegates.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RESTUtil {

    private static final String TAG = RESTUtil.class.getCanonicalName();

    private static final int REQUEST_READ_TIMEOUT = 100;

    //TODO: Replace this with the actual AUTO COMPLETE URL
    private static String AUTO_COMPLETE_URL = "http://www.google.com";

    public static void makeAutoCompleteRequest(final Callback<ArrayList<String>> autoCompleteCallback) {

        final OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(REQUEST_READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        final Request recordRequest = new Request.Builder()
                .url(AUTO_COMPLETE_URL).build();

        /* Make call to auto-complete api */
        client.newCall(recordRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Record request failed...");
                autoCompleteCallback.onExecuted(new ArrayList<String>());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                /* Om Success return auto-complete Address results to callback */
                String responseString = response.body().string();
                ArrayList<String> suggestions = parseSuggestionList(responseString);
                autoCompleteCallback.onExecuted(suggestions);
            }
        });

    }

    /* TODO: Replace this method's logic here with ACTUAL AUTOCOPLETE LOGIC.  A real address autocomplete list
    *  TODO: needs to be returned instead of this dummy random list*/
    private static ArrayList<String> parseSuggestionList(String response) {

        ArrayList<String> suggestions = new ArrayList<>();

        int number = (int)(Math.random()*10);

        for(int i = 0; i < number; i++) {
            suggestions.add(Math.random() * 100 + "");
        }

        return suggestions;
    }

}
