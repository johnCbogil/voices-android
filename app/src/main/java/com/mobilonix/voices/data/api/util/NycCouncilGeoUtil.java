package com.mobilonix.voices.data.api.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.mobilonix.voices.base.util.GeneralUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Converts NYC lat / lon to physical address String and provides address String and mBorough
 *
 *
 *
 * TODO Designed to work with {@link .api.engines.NycCouncilApi} but could be generalized or
 * TODO    substituted
 * TODO this entire thing needs to be refactored
 */

public class NycCouncilGeoUtil {

    Geocoder mGeocoder;
    int mBorough;
    String mAddressLine;
    List<Address> mAddresses = null;

    public static final int NUMBER_OF_LISTINGS = 5;

    public NycCouncilGeoUtil(Context context) {
        mGeocoder = new Geocoder(context);
    }

    public void init(double lat, double lon){

        try {
            mAddresses = mGeocoder.getFromLocation(lat, lon, NUMBER_OF_LISTINGS);

        } catch (IOException ioException) {
            Log.e("mGeocoder", ioException.toString());

        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e("mGeocoder", illegalArgumentException.toString());
        }

        parseAddressLine();
        parseBorough();
    }

    //TODO switch to regex match in order to take advantage of matches() method for multiple terms
    private void parseBorough() {
        String allAddys = null;
        mBorough = 0;

        try {
            allAddys = mAddresses.toString().toLowerCase();
        } catch (Exception e) {
            //FIXME add proper exception handling
            Log.e("NycCouncilGeoUtil","Problem parsing boroughs");
        }

        if (allAddys != null && allAddys.contains(", ny")) {
            if (allAddys.contains("staten island")) {
                mBorough = 5;
            } else if (allAddys.contains("bronx")) {
                mBorough = 2;
            } else if (allAddys.contains("brooklyn")) {
                mBorough = 3;
            } else if (allAddys.contains("queens")
                    || allAddys.contains("long island city")) {
                mBorough = 4;
            } else if (allAddys.contains("manhattan")) {
                mBorough = 1;
            }
        }
    }

    private void parseAddressLine() {

        Log.i("maddresses", "" + mAddresses);

        if(mAddresses != null) {

            for (int i = 0; i < mAddresses.size(); i++) {
                if (mAddressLine == null) mAddressLine = mAddresses.get(i).getAddressLine(0);
            }
            Log.d("TAG", "address: " + mAddressLine);
        }
    }

    public String getAddressLine() {
        return  mAddressLine;
    }
    public String getBorough() {
        return Integer.toString(mBorough);
    }
    
    public boolean isNyc() {
        return mBorough != 0;
    }
}
