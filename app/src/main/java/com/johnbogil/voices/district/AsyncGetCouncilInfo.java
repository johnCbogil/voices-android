package com.johnbogil.voices.district;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.johnbogil.voices.activities.FragmentDisplay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class downloads an MGID list stored on S3 for a specific brand / tier. Eventually it will
 * be used to download all receiver information rather than storing it as resource assets.
 *
 */

public class AsyncGetCouncilInfo extends AsyncTask<Object, Void, Integer> {

    static final String TAG = "Async";

    public interface OnCouncilDataReady {

        void dataReady(int district);
    }

    OnCouncilDataReady councilDataReady;

    public AsyncGetCouncilInfo(OnCouncilDataReady fragmentDisplay) {
        Log.i("CouncilAsync", "Constructor");
        councilDataReady = (OnCouncilDataReady) fragmentDisplay;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Object... params) {
        Log.i("CouncilAsync", "doInBackground");

        String address = (String) params[0];
        int borough = (int) params[1];

        Log.d("execute","0: " + params[0] + "1: " + params[1]);

        return (Integer) getDistrict("http://legistar.council.nyc.gov/redirect.aspx",address,borough);
    }

    @Override
    protected void onPostExecute(Integer district) {
        councilDataReady.dataReady(district);
        super.onPostExecute(district);
    }

    public int getDistrict(String url, String address, int borough)  {

        String result = "";

        HashMap<String, String> params = new HashMap<String, String>();

        try {

            byte[] values = ("lookup_address=" + address + "&lookup_borough=" + borough).getBytes("UTF-8");

            URL councilURL = new URL(url);

            if ((url == null) || (url.equals(""))) {
                throw new IllegalArgumentException("Url is null or empty!");
            }

            HttpURLConnection poster = (HttpURLConnection) councilURL.openConnection();

            poster.setConnectTimeout(5000);
            poster.setReadTimeout(5000);

            poster.setRequestMethod("POST");
            poster.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            poster.setDoOutput(true);
            poster.getOutputStream().write(values);

            BufferedReader inputStream;
            String str;
            inputStream = new BufferedReader(new InputStreamReader(poster.getInputStream(), "UTF-8"));

            while ((str = inputStream.readLine()) != null) {
                //Log.d("async", str);
                result += str;
            }

        } catch (IOException e) {

        }

        int breadCrumb = result.indexOf("District");

        if(breadCrumb != -1) {
            return Integer.parseInt(result.substring(breadCrumb + 9, breadCrumb + 11).trim());
        }

        return -1;
    }

    ArrayList<String> defaultDistrict(String message){

        ArrayList<String> data = null;

        return data;
    }
}