package com.johnbogil.voices.district;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by cakiadeg on 4/24/16.
 */

//TODO Should be named NycCouncilGeoUtil
public class GeoUtil {

    Geocoder geocoder;
    int borough;
    String addressLine;
    List<Address> addresses = null;

    public GeoUtil(Context context, double lat, double lon) {

        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        parseAddressLine();
        parseBorough();
    }

    private void parseBorough() {

        if (addresses != null) {
            if (addresses.toString().toLowerCase().contains("staten island")) {
                borough = 5;
            } else if (addresses.toString().toLowerCase().contains("bronx")) {
                borough = 2;
            } else if (addresses.toString().toLowerCase().contains("brooklyn")) {
                borough = 3;
            } else if (addresses.toString().toLowerCase().contains("queens")) {
                borough = 4;
            } else if (addresses.toString().toLowerCase().contains("manhattan")) {
                borough = 1;
            }
        }
    }

    private void parseAddressLine() {

        if(addresses != null) {

            for (int i = 0; i < addresses.size(); i++) {
                if (addressLine == null) addressLine = addresses.get(0).getAddressLine(0);
            }
            Log.d("TAG", "address: " + addressLine);
        }
    }

    public String getAddressLine() {
        return  addressLine;
    }

    public int getBorough() {
        return borough;
    }
}
