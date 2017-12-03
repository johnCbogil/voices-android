package com.mobilonix.voices.util;

import android.util.Log;

import com.mobilonix.voices.callbacks.Callback2;
import com.mobilonix.voices.data.model.Politico;
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

    private static final int REQUEST_READ_TIMEOUT = 20;

    //Make a request for the representatives list.  This should be obtained from the UI (via the user choice)

    public static void makeRepresentativesRequest(String address,
                                                  double repLat,
                                                  double repLong,
                                                  final RepresentativesManager.RepresentativesType type,
                                                  final Callback2<ArrayList<Representative>,
                                                          RepresentativesManager.RepresentativesType>
                                                          representativesCallback) {
        final OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(REQUEST_READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        //Make call to auto-complete api
        try {
            //Create a new API request based on the representative type. Federal uses address and
            //State and city use lat long
            Request request = null;
            if(type == RepresentativesManager.RepresentativesType.CONGRESS) {
                request = type.getRequestForFederal(address);
            } else if(type == RepresentativesManager.RepresentativesType.STATE_LEGISLATORS) {
                request = type.getRequestForState(repLat, repLong);
            } else {
                request = type.getRequestForState(repLat, repLong);
            }

            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Record request failed..." + e);
                    representativesCallback.onExecuted(new ArrayList<Representative>(), type);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                /* Om Success return auto-complete Address results to callback */
                    String responseString = response.body().string();
                    ArrayList<Representative> representatives = parseRepresentativesList(responseString, type);
                    representativesCallback.onExecuted(representatives, type);
                    response.body().close();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "API request failed..." + e);
            representativesCallback.onExecuted(new ArrayList<Representative>(), type);
        }
    }

    private static ArrayList<Representative> parseRepresentativesList(String response,
                                                                      RepresentativesManager
                                                                              .RepresentativesType type) {

        ArrayList<Representative> representatives = new ArrayList<>();
        ArrayList<Politico> politicos = type.parseJsonResponse(response);

        //really this should be handled by throwing an error
        if(politicos == null || politicos.isEmpty()) {
            return null;
        }

        for(Politico poli : politicos) {

            representatives.add(
                    new Representative(
                            "TITLE",
                            poli.getFullName(),
                            "LOCATION",
                            poli.getParty(),
                            poli.getDistrict(),
                            poli.getElectionDate(),
                            poli.getPhoneNumber(),
                            poli.getTwitterHandle(),
                            poli.getContactForm(),
                            poli.getEmailAddress(),
                            poli.getPicUrl(),
                            poli.getLevel()));
        }

        return representatives;
    }
}
