package com.mobilonix.voices.data.api.engines;

import android.content.Context;
import android.util.Log;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.util.NycCouncilFilter;
import com.mobilonix.voices.data.api.util.NycCouncilGeoUtil;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.representatives.RepresentativesManager;

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

public class NycLocalOfficialsApi implements ApiEngine {

    //FIXME this is a temporary way to convert from lat/lon to address

    static  NycCouncilGeoUtil geoUtil = new NycCouncilGeoUtil(VoicesApplication.getContext());

    public static final String BASE_URL = "http://legistar.council.nyc.gov/redirect.aspx";
    public static final String ADDRESS_KEY = "lookup_address";
    public static final String BOROUGH_KEY = "lookup_borough";

    public static final String POST_CONTENT_KEY = "Content-Type";
    public static final String POST_CONTENT_VALUE = "application/x-www-form-urlencoded";

    static final String TAG = "NycLocalOfficialsApi";

    double mLatitude, mLongitude;

    @Override
    public Request generateRequest(double latitude, double longitude) {

        mLatitude = latitude;
        mLongitude = longitude;

        Log.i("huh","lat: " + latitude + " lon: " + longitude);

        geoUtil.init(latitude, longitude);

        String address = geoUtil.getAddressLine();
        String borough = geoUtil.getBorough();

        //uncomment below for testing
            //address = "1515 Broadway"
            //borough = "1";

        Log.i(TAG, "address: " + address + " borough: " + borough) ;

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
        Politico mayor = getMayor();

        if(politico != null) {
            politicos.add(politico);
            politicos.add(mayor);
        }
        return politicos;
    }

    public Politico politicianFromDistrict(Integer district) {

        Log.i(TAG, "politician from disctrict: " + district);

        if (!(district >= 1 && district <= 51)) {
            district = NycCouncilFilter.filterDistrict(VoicesApplication.getContext(), mLatitude, mLongitude);
            Log.i("nycapi", "in here district: " + district);
        }

        try {
            JSONObject districts = getJsonFromResource(VoicesApplication.getContext(), R.raw.nyc_district_data);

            Log.i(TAG, "0: " + districts.toString());

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
                    .build(firstName, lastName);

            return politico;

        } catch (JSONException e) {
            Log.e(TAG, "json parse: " + e);

            return null;
        }
    }

    public int getDistrict(String response)  {

        //Uses clue word "District" to find location of district #
        int breadCrumb = response.indexOf("District");
        String subset = response.substring(breadCrumb + 9, breadCrumb + 11).trim();

        Matcher matcher = Pattern.compile("\\d+").matcher(subset);

        if( matcher.find() ) {
            return Integer.valueOf(matcher.group());
        }

        return 0;
    }

    public Politico getMayor () {
        try {
            JSONObject mayor = getJsonFromResource(VoicesApplication.getContext(), R.raw.bill_de_blasio);

            String firstName = mayor.getString("firstName");
            String lastName = mayor.getString("lastName");
            String phoneNumbers = mayor.getString("phoneNumber");
            String photos = mayor.getString("photoURLPath");
            String twitter = mayor.getString("twitter");
            String email = mayor.getString("email");

            Politico politico = new Politico.Builder()
                    .setEmailAddy(email)
                    .setPhoneNumber(phoneNumbers)
                    .setPicUrl(photos)
                    .setTwitterHandle(twitter)
                    .build(firstName,lastName);

            return politico;

        } catch (JSONException e) {
            Log.e(TAG,"json parse: " + e);
            return null;
        }
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

    @Override
    public RepresentativesManager.RepresentativesType getRepresentativeType() {
        return RepresentativesManager.RepresentativesType.COUNCIL_MEMBERS;
    }
}