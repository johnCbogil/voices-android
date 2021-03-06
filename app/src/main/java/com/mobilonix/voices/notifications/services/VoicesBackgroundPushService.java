package com.mobilonix.voices.notifications.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mobilonix.voices.session.SessionManager;

public class VoicesBackgroundPushService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SessionManager.INSTANCE.setCurrentNotificationToken(refreshedToken);
    }

}
