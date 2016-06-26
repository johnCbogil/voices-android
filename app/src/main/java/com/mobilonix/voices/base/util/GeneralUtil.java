package com.mobilonix.voices.base.util;

import android.content.Context;
import android.location.LocationManager;
import android.widget.Toast;

import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.VoicesMainActivity;

public class GeneralUtil {

    /**
     * Check if GPS (FINE Location Resolution) is enabled
     *
     * @param context
     * @return
     */
    public static boolean isGPSEnabled(Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        return gpsEnabled;
    }

    /**
     * Check if network location (COARSE Location Resolution) is enabled
     *
     * @param context
     * @return
     */
    public static boolean isNetworkLocationEnabled(Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkEnabled = false;

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        return networkEnabled;
    }

    public static void toast(String message) {
        Toast.makeText(VoicesApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}