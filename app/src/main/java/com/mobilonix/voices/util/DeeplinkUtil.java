package com.mobilonix.voices.util;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.mobilonix.voices.callbacks.Callback;

public class DeeplinkUtil {

    public final static String TAG = DeeplinkUtil.class.getCanonicalName();

    public static void parseDeeplink(Intent intent, Callback<String> callback) {
        try {
            String action = intent.getAction();
            Uri data = intent.getData();
            if(data != null) {
                String groupKey =  data.getPath().replace("/", "");
                callback.onExecuted(groupKey);
            } else {
                Log.e(TAG, "We didn't get a deeplink");
            }
        } catch (Exception e) {
            Log.e(TAG, "We didn't get a deeplink");
        }
    }

}
