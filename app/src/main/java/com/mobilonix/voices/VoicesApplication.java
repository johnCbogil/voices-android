package com.mobilonix.voices;

import android.app.Application;
import android.content.Context;

public class VoicesApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static Context getContext() {
        return context;
    }
}
