package com.mobilonix.voices.session;

public enum SessionManager {

    INSTANCE;

    private String currentNotificationToken;

    public void setCurrentNotificationToken(String currentNotificationToken) {
        this.currentNotificationToken = currentNotificationToken;
    }

    public String getCurrentNotificationToken() {
        return currentNotificationToken;
    }
}
