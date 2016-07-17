package com.mobilonix.voices.data.api.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncGetLatLonFromAddy extends AsyncTask<String, String, String> {

        Context context;

        AsyncGetLatLonFromAddy(Context context) {

            this.context = context;

        }

        @Override
        protected String doInBackground(String... params) {

            try{
                URL url = new URL(  params[0] );
                String address = params[1];

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {

                    StringBuilder sb = new StringBuilder();
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        Log.d("    inputLine", inputLine);
                        sb.append(inputLine).append("\n");
                    }

                    in.close();
                    return sb.toString();
                } finally {
                    urlConnection.disconnect();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute (String result){
            Log.d("Goog url", "JSON goog api Data" + result);

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
            } catch ( JSONException e) {

            }

            NycCouncilGeoUtil geoUtil = new NycCouncilGeoUtil(context, );
        }
    }
}
