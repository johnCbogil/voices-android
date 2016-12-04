package com.mobilonix.voices.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.delegates.Callback2;
import com.mobilonix.voices.representatives.RepresentativesManager;
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
                response.body().close();
            }
        });

    }

    /**
     * Make a request for the representatives list.  This should be obtained from the UI (via the user choice)
     *
     * TODO: This is just a dummy method for now. The logic here needs to be replaced with the actual parsing logic
     *
     * @param repLat
     * @param repLong
     * @param type (The type of representatives.  This will inform what URL and params are used to make the request)
     * @param representativesCallback
     */

    public static void makeRepresentativesRequest(double repLat,
                                                  double repLong,
                                                  final RepresentativesManager.RepresentativesType type,
                                                  final Callback2<ArrayList<Representative>,
                                                          RepresentativesManager.RepresentativesType>
                                                          representativesCallback) {
        final OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(REQUEST_READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        /* Make call to auto-complete api */
        try {
            if (isOnline()==false) {
                GeneralUtil.toast("REP REQUEST FAILED!");
                representativesCallback.onExecuted(DatabaseUtil.fetchRepresentatives(type.getIdentifier()), type);

                // There are no active networks.
                //TODO: if you are offline then fetch from cache and then call representativesCallback.onExecuted with SharedPreferences key and value
            } else {
                client.newCall(type.getRequest(repLat, repLong)).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Record request failed..." + e);
                        ArrayList<Representative> representatives = DatabaseUtil.fetchRepresentatives(type.getIdentifier());
                        representativesCallback.onExecuted(representatives, type);

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e(TAG, "Record request success... with response" + response);
                        // On success return auto-complete address results to callback
                        String responseString = response.body().string();
                        ArrayList<Representative> representatives = parseRepresentativesList(responseString, type);
                        //this is executed only if you are online and have a successful request
                        representativesCallback.onExecuted(representatives, type);
                        response.body().close();
                    }
                });
            }
            }catch(IOException e){
                Log.e(TAG, "API request failed..." + e);
                representativesCallback.onExecuted(new ArrayList<Representative>(), type);
            }
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
    private static ArrayList<Representative> parseRepresentativesList(String response,
                                                                      RepresentativesManager
                                                                              .RepresentativesType type) {

        ArrayList<Representative> representatives = new ArrayList<>();
        ArrayList<Politico> politicos = type.parseJsonResponse(response);

        //todo make below error mitigation line display a dialog
        //Really this should be handled by throwing an error
        if(politicos == null || politicos.isEmpty()) return null;

        for(Politico poli : politicos) {

            representatives.add(
                    new Representative(
                            "TITLE",
                            poli.getFullName(),
                            "LOCATION",
                            poli.getGender(),
                            poli.getParty(),
                            poli.getDistrict(),
                            poli.getElectionDate(),
                            poli.getPhoneNumber(),
                            poli.getTwitterHandle(),
                            poli.getEmailAddy(),
                            poli.getPicUrl()));
        }

        return representatives;
    }

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

    public static boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)VoicesApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
