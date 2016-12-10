package com.mobilonix.voices.analytics;

import com.mobilonix.voices.BuildConfig;
import com.mobilonix.voices.delegates.Callback2;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public enum AnalyticsManager {

    INSTANCE;

    public boolean logDebugEvents = true;

    public static final String TAG = AnalyticsManager.class.getCanonicalName();
    public static String ANALYTICS_URL
                = "https://script.google.com/macros/s/AKfycbxBK6HTkA6tTXU09sRF5PHHCq2LpBOFdx4ZH7E4ORf3sG374iU/exec";

    public static final String UNSUBSCRIBE_EVENT = "UNSUBSCRIBE_EVENT";
    public static final String SUBSCRIBE_EVENT = "SUBSCRIBE_EVENT";

                public static int ANALYTICS_REQUEST_TIMEOUT = 5000;

                public void trackEvent(String eventName,
                                                                   String eventFocus,
                                                                   String loggingId,
                                                                   String eventData,
                                                                   final Callback2<String, Boolean> callback) {

                        if(!logDebugEvents && BuildConfig.DEBUG) {
                        return;
                    }

                        if(BuildConfig.DEBUG) {
                                eventName = "DEBUG_" + eventName;
                    }

                        final OkHttpClient client = new OkHttpClient.Builder()
                                .readTimeout(ANALYTICS_REQUEST_TIMEOUT, TimeUnit.SECONDS)
                                .build();

                        final Request recordRequest = new Request.Builder()
                                .url(ANALYTICS_URL + "?eventType=" + eventName +
                                "&eventFocus=" + eventFocus +
                                "&eventLoggerId=" + loggingId +
                                "&eventData=" + eventData +
                                "&platform=" + "Android" +
                                "&osVersion=" + android.os.Build.VERSION.RELEASE).build();

                        /* Make call to auto-complete api */
                                client.newCall(recordRequest).enqueue(new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                                if(callback != null) {
                                                        callback.onExecuted("ERROR", false);
                                                    }
                                            }

                                                @Override
                                        public void onResponse(Call call, Response response) throws IOException {

                                                        /* On Success return auto-complete Address results to callback */
                                                                String responseString = response.body().string();
                                                if(callback != null) {
                                                        callback.onExecuted(responseString, true);
                                                    }
                                                response.body().close();
                                            }
                                    });

                    }

                public void shouldLogDebugEvents(boolean status) {
                logDebugEvents = status;
            }
    }