package com.mobilonix.voices.data.api.util;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.mobilonix.voices.VoicesApplication;

import java.io.IOException;
import java.util.List;

/**
 * Created by cakiadeg on 9/18/16.
 */

public class VoicesGeoUtil {

    String mAddressLine;
    String mState;
    String mCity;

    public static final int NUMBER_OF_LISTINGS = 5;

    public VoicesGeoUtil() {}

    public VoicesGeoUtil(double lat, double lon) {
        resetLocation(lat,lon);
    }

    public List<Address> resetLocation(double lat, double lon){

        Geocoder geocoder = new Geocoder(VoicesApplication.getContext());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(lat, lon, NUMBER_OF_LISTINGS);
        } catch (IOException ioException) {
            Log.e("mGeocoder", ioException.toString());

        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e("mGeocoder", illegalArgumentException.toString());
        }

        setLocationData(addresses);

        return addresses;
    }

    private void setLocationData(List<Address> addresses) {

        Log.i("maddresses", "" + addresses);

        if(addresses != null) {

            for (Address address : addresses) {

                if(mAddressLine == null) mAddressLine = address.getAddressLine(0);
                if(mState == null) mState = address.getAdminArea();
                if(mCity == null) mCity = address.getLocality();

            }

            Log.d("TAG", "addresses: " + addresses);
            Log.d("TAG", "address: " + mAddressLine);
            Log.d("TAG", "state: " + mState);
            Log.d("TAG", "city: " + mCity);
        }
    }

    public String getAddressLine() {
        return  mAddressLine;
    }

    public String getState() {
        return mState;
    }

    public String getCity() {
        return mCity;
    }
}