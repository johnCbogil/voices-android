package com.mobilonix.voices.notifications.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.session.SessionManager;

public class VoicesBackgroundPushService extends FirebaseInstanceIdService {

    private final static String TAG
            = VoicesBackgroundPushService.class.getCanonicalName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        GeneralUtil.toast("New token: " + refreshedToken);

        SessionManager.INSTANCE.setCurrentNotificationToken(refreshedToken);
    }

}
