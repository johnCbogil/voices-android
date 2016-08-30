package com.mobilonix.voices.data.api.engines;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.util.UrlGenerator;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.representatives.RepresentativesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Request;

public class StateOpenStatesApi implements ApiEngine {

    //  legislator strings

    public static final String BASE_URL = "http://openstates.org/api/v1//legislators/geo/";
    public static final String LATITUDE_KEY = "lat";
    public static final String LONGITUDE_KEY = "long";
    public static final String API_KEY = "apikey";

    public static final String API_VALUE = "e39ba83d7c5b4e348db144c4b4c33108";

    public StateOpenStatesApi() {

        Log.i("response","in openstates" );

    }

    @Override
    public Request generateRequest(double latitude, double longitude) {

        Bundle urlBundle = new Bundle();

        urlBundle.putString(LATITUDE_KEY, Double.toString(latitude));
        urlBundle.putString(LONGITUDE_KEY, Double.toString(longitude));
        urlBundle.putString(API_KEY, API_VALUE);

        UrlGenerator generator = new UrlGenerator(BASE_URL, urlBundle);

        Request recordRequest = new Request.Builder()
                .url(generator.generateGetUrl())
                .build();

        return recordRequest;
    }

    @Override
    public ArrayList<Politico> parseData(String response){

        Log.i("response", response);

        ArrayList<Politico> politicos = new ArrayList<>();

        try {

            JSONArray rawJsonArray = new JSONArray(response);

            for(int i = 0; i < rawJsonArray.length(); i++){

                JSONObject jsonPolitico = rawJsonArray.getJSONObject(i);

                String chamber = jsonPolitico.getString("chamber");
                String title = setTitle(chamber);
                String fullName = title + jsonPolitico.getString("full_name");
                String email = jsonPolitico.getString("email");
                String phoneNumber = jsonPolitico.getJSONArray("offices").getJSONObject(0).getString("phone");
                String picUrl = jsonPolitico.getString("photo_url");

                Politico politico = new Politico.Builder()
                        .setEmailAddy(email)
                        .setPhoneNumber(phoneNumber)
                        .setPicUrl(picUrl)
                        .build(fullName);

                politicos.add(politico);
            }

//            JSONObject districts = getJsonFromResource(VoicesApplication.getContext(), R.raw.nyc_district_data);
//            Log.i(TAG, "0: " + districts.toString());
//
//            JSONObject member = districts.getJSONObject("districts");
//            Log.i(TAG, "1: " + member);
//            member = member.getJSONObject(district + "");
//            Log.i(TAG, "2: " + member);
//
//            String firstName = member.getString("firstName");
//            String lastName = member.getString("lastName");
//            String phoneNumbers = member.getString("phoneNumber");
//            String photos = member.getString("photoURLPath");
//            String twitter = member.getString("twitter");
//            String email = member.getString("email");
//
//            Politico politico = new Politico.Builder()
//                    .setEmailAddy(email)
//                    .setPhoneNumber(phoneNumbers)
//                    .setPicUrl(photos)
//                    .setTwitterHandle(twitter)
//                    .build(firstName, lastName);
//
//            return politico;
        } catch (JSONException e) {

            e.printStackTrace(); //TODO handle exception
        }

        return politicos;
    }

    public static JSONObject getJsonFromResource(Context context, int jsonResource)  {
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

    private static String convertStreamToString(InputStream inputStream) {

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

    public String setTitle(String chamber){
        if (chamber.equals("upper")) {
            return "Sen ";
        } else if (chamber.equals("lower")){
            return "Rep ";
        } else{
            return "";
        }
    }

    @Override
    public RepresentativesManager.RepresentativesType getRepresentativeType() {
        return RepresentativesManager.RepresentativesType.STATE_LEGISLATORS;
    }
}
