package com.mobilonix.voices;

import android.app.Application;
import android.content.Context;

import com.badoo.mobile.util.WeakHandler;

public class VoicesApplication extends Application {

    private static Context context;
    private static WeakHandler globalHandler;

    public final static String EMPTY = "";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static void setGlobalHandler(WeakHandler globalHandler) {
        VoicesApplication.globalHandler = globalHandler;
    }

    public static WeakHandler getGlobalHandler() {
        return globalHandler;
    }

    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(base);
    }

    public static Context getContext() {
        return context;
    }

}
