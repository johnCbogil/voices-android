package com.mobilonix.voices;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class VoicesApplication extends Application {

    private static Context context;

    public final static String EMPTY = "";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static Context getContext() {
        return context;
    }

}
