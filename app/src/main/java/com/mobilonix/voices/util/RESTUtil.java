package com.mobilonix.voices.util;

import android.util.Log;

import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.representatives.model.Representative;

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

    public static void makeRepresentativesRequest(final Callback<ArrayList<Representative>> representativesCallback) {
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
                representativesCallback.onExecuted(new ArrayList<String>());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                /* Om Success return auto-complete Address results to callback */
                String responseString = response.body().string();
                ArrayList<String> suggestions = parseSuggestionList(responseString);
                representativesCallback.onExecuted(suggestions);
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

    /* TODO: Replace this method's logic here with ACTUAL AUTOCOPLETE LOGIC.  A real address autocomplete list
    *  TODO: needs to be returned instead of this dummy random list*/
    private static ArrayList<Representative> parseRepresentativesList(String response) {

        ArrayList<String> dummyPhoneNumbers = new ArrayList<>();
        ArrayList<String> dummyNames = new ArrayList<>();
        ArrayList<String> dummyTitles = new ArrayList<>();
        ArrayList<String> dummyTwitterHandles = new ArrayList<>();
        ArrayList<String> dummyEmailAddresses = new ArrayList<>();
        ArrayList<String> dummyImageUrls = new ArrayList<>();
        ArrayList<String> dummyLocations = new ArrayList<>();

        dummyPhoneNumbers.add("2012220418");
        dummyPhoneNumbers.add("2012599877");
        dummyPhoneNumbers.add("2012599877");

        dummyTitles.add("2012220418");
        dummyTitles.add("2012599877");
        dummyTitles.add("2012599877");

        dummyNames.add("Buster McSneedy");
        dummyNames.add("Phil Mckraken");
        dummyNames.add("Jason Bourne");

        dummyTwitterHandles.add("Buster McSneedy");
        dummyTwitterHandles.add("Phil Mckraken");
        dummyTwitterHandles.add("Jason Bourne");

        dummyEmailAddresses.add("bmcsneedy123123@gmail.com");
        dummyEmailAddresses.add("pcraker69@gmail.com");
        dummyEmailAddresses.add("deathhole19991@verizon.net");

        dummyImageUrls.add("bmcsneedy123123@gmail.com");
        dummyImageUrls.add("pcraker69@gmail.com");
        dummyImageUrls.add("deathhole19991@verizon.net");

        dummyLocations.add("Camden, NJ");
        dummyLocations.add("Asshole, NY");
        dummyLocations.add("XYZ, MX");


        ArrayList<Representative> representatives = new ArrayList<>();

        int number = (int)(Math.random()*10);


        for(int i = 0; i < number; i++) {

            String name = dummyNames.get(getRandomInt(0, dummyNames.size()));
            String title = dummyTitles.get(getRandomInt(0, dummyTitles.size()));
            String phoneNumber = dummyPhoneNumbers.get(getRandomInt(0, dummyPhoneNumbers.size()));
            String twitterHandle = dummyTwitterHandles.get(getRandomInt(0, dummyTwitterHandles.size()));
            String emailAddress = dummyEmailAddresses.get(getRandomInt(0, dummyEmailAddresses.size()));
            String imageUrl = dummyImageUrls.get(getRandomInt(0, dummyImageUrls.size()));
            String location = dummyLocations.get(getRandomInt(0, dummyLocations.size()));

            representatives.add(
                    new Representative(
                            title,
                            name,
                            location,
                            phoneNumber,
                            twitterHandle,
                            emailAddress,
                            imageUrl));

        }

        return representatives;
    }

//    String title,
//    String name,
//    String location,
//    String phoneNumber,
//    String twitterHandle,
//    String emailAddress,
//    String representativeImageUrl

    /**
     * Random integer
     *
     * @param lowerBound
     * @param upperBound
     * @return
     */
    public static int getRandomInt(int lowerBound, int upperBound)  {

        return (int)((upperBound - lowerBound)*Math.random() + lowerBound);
    }

}
