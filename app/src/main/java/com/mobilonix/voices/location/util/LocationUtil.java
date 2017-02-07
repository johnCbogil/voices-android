package com.mobilonix.voices.location.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.callbacks.Callback;
import com.mobilonix.voices.location.model.LatLong;

import java.util.List;

public class LocationUtil {

    private final static String TAG = LocationUtil.class.getCanonicalName();

    private static int LOCATION_CHECK_INTERVAL = 1000;

    private static boolean initialized = false;

    private static LocationManager locationManager;
    private static LatLong lastLocation = new LatLong(0, 0);
    private static boolean singlePoll = true;

    /* This callback is set up so that a requester can send location update triggers
    * back to the app.  Note: This is currently not set up to be a multi-cast callback because
    * we shouldn't be in a situation where we need multiple people requesting */
    private static Callback<LatLong> locationRequestCallback = null;

    private static boolean checkLocationRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(VoicesApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(VoicesApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method lets the Location utility know if it should repeated poll the GPS or only once
     *
     * @param state
     */
    public static void shouldSinglePoll(boolean state) {
        singlePoll = state;
    }

    /**
     * This will return the last recorded GPS location.  We use this
     *
     * @return
     */
    public static LatLong getLastLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        LatLong lastLocation = new LatLong(0,0);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return lastLocation;
        }


        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }

        try {
            lastLocation = new LatLong(bestLocation.getLatitude(), bestLocation.getLongitude());
        } catch (Exception e) {

            /* TODO: Need to replace this with location caching through shared prefs logic */
            lastLocation = new LatLong(0, 0);
        }

        return lastLocation;
    }

    /**
     * Check if GPS (FINE Location Resolution) is enabled
     *
     * @param context
     * @return
     */
    public static boolean isGPSEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        return gpsEnabled;
    }

    /**
     * Check if network location (COARSE Location Resolution) is enabled
     *
     * @param context
     * @return
     */
    public static boolean isNetworkLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkEnabled = false;

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return networkEnabled;
    }

    /**
     * Remove the listener for location updates
     *
     * @param listener
     */
    public static void stopLocationUpdates(LocationListener listener) {
        if (ActivityCompat
                .checkSelfPermission(VoicesApplication.getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(VoicesApplication.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ((LocationManager) VoicesApplication.getContext()
                .getSystemService(Context.LOCATION_SERVICE)).removeUpdates(listener);
    }

    /**
     * Force a location update
     *
     * @param listener
     * @param callback
     */
    public static void triggerLocationUpdate(LocationListener listener, Callback<LatLong> callback) {

        if (ActivityCompat.checkSelfPermission(VoicesApplication.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(VoicesApplication.getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        ((LocationManager) VoicesApplication.getContext()
                        .getSystemService(Context.LOCATION_SERVICE))
                .requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_CHECK_INTERVAL, 0, listener);

        locationRequestCallback = callback;
    }

    public static void setLocationRequestCallback(Callback callback) {
        locationRequestCallback = callback;
    }

    public static Callback<LatLong> getLocationRequestCallback() {
        return locationRequestCallback;
    }
}
