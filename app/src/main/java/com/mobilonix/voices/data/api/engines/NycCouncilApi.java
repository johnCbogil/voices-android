package com.mobilonix.voices.data.api.engines;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.util.NycCouncilGeoUtil;
import com.mobilonix.voices.data.model.Politico;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NycCouncilApi implements ApiEngine {

    //FIXME this is a temporary way to convert from lat/lon to address

    static  NycCouncilGeoUtil geoUtil = new NycCouncilGeoUtil(VoicesApplication.getContext());

    public static final String BASE_URL = "http://legistar.council.nyc.gov/redirect.aspx";
    public static final String ADDRESS_KEY = "lookup_address";
    public static final String BOROUGH_KEY = "lookup_borough";

    public static final String POST_CONTENT_KEY = "Content-Type";
    public static final String POST_CONTENT_VALUE = "application/x-www-form-urlencoded";

    static final String TAG = "response";

    public NycCouncilApi() {
        Log.i("response","in nyccouncil" );

    }

    @Override
    public Request generateRequest(double latitude, double longitude) {

        geoUtil.init(latitude, longitude);

        Log.i("nyc", "lat: " + latitude + " lon: " +longitude) ;

        String address = geoUtil.getAddressLine();

        Log.i(TAG,"district addy:" + address);

        //address = "1515 broadway";
        String borough = geoUtil.getBorough();
        //borough = "1";

        Log.i("nyc", "address: " + address + " borough: " + borough) ;

        URL url;

        try {
            url = new URL(BASE_URL);
        } catch(MalformedURLException a) {
            url = null;
        }

        if(address != null && !borough.equals("0")) {
            Headers headers = new Headers.Builder()
                    .add(POST_CONTENT_KEY, POST_CONTENT_VALUE)
                    .build();

            RequestBody body = new FormBody.Builder()
                    .add(ADDRESS_KEY, address)
                    .add(BOROUGH_KEY, borough)
                    .build();

            Request recordRequest = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .post(body)
                    .build();

            return recordRequest;
        }
        return  null;
    }

    @Override
    public ArrayList<Politico> parseData(String response) throws IOException {

        ArrayList<Politico> politicos = new ArrayList<>();

        Politico politico = politicianFromDistrict(getDistrict(response));

        if(politico != null)  politicos.add(politico);

        return politicos;
    }

    public Politico politicianFromDistrict(Integer district) {

        Log.i("response","politician from disctrict: " + district);

        try {
            JSONObject districts = getJsonFromResource(VoicesApplication.getContext(), R.raw.nyc_district_data);

            Log.i(TAG,"0: " + districts.toString());

            JSONObject member = districts.getJSONObject("districts");
            Log.i(TAG, "1: " + member);
            member = member.getJSONObject(district + "");
            Log.i(TAG, "2: " + member);


            String firstName = member.getString("firstName");
            String lastName = member.getString("lastName");
            String phoneNumbers = member.getString("phoneNumber");
            String photos = member.getString("photoURLPath");
            String twitter = member.getString("twitter");
            String email = member.getString("email");

            Politico politico = new Politico.Builder()
                    .setEmailAddy(email)
                    .setPhoneNumber(phoneNumbers)
                    .setPicUrl(photos)
                    .setTwitterHandle(twitter)
                    .build(firstName,lastName);

            return politico;

        } catch (JSONException e) {
            Log.e("response","json parse: " + e);
            return null;
        }
    }


    public int getDistrict(String response)  {

        int breadCrumb = response.indexOf("District");
        Log.i("geocoder", "district #: " + response.substring(breadCrumb + 9, breadCrumb + 11).trim().replace("<","") );

        Matcher matcher = Pattern.compile("\\d+").matcher(response);
        matcher.find();

        if(matcher.matches()) {
            return Integer.valueOf(matcher.group());
        }

        return 0;
    }

    private JSONObject getJsonFromResource(Context context, int jsonResource)  {

        InputStream inputStream = context.getResources().openRawResource(jsonResource); // getting XML

        if(jsonResource > 0){

            try {
                JSONObject jsonObject = new JSONObject(convertStreamToString(inputStream));

                Log.d("TAG", jsonObject.toString());
                return jsonObject;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String convertStreamToString(InputStream inputStream) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

            //TODO handle exception
        }
        return outputStream.toString();
    }
}