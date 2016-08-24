package com.mobilonix.voices.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.engines.NycLocalOfficialsApi;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author George Cakiades
 *
 * This is a stub, will be filled out at a later time when more local APIs are on-boarded.
 *
 * This utility will provide Geocoding and reverse Geocoding services as needed throughout the app.
 * Firstly, it will be used in {@link com.mobilonix.voices.representatives.RepresentativesManager }
 * to decide which loca officials API to call
 *
 */
public class GeocoderUtil {

    private Geocoder geoUtil;

    public static final int MAX_RESULTS = 5;

    public GeocoderUtil(Context context) {
        geoUtil = new Geocoder(context);
    }

    //STUB
    public String getCityFromLatLon(double lat, double lon) {
        return null;
    }

    //STUB
    public String getStateFromLatLon(double lat, double lon) {
        return null;
    }

    public ApiEngine getLocalApi(double lat, double lon) {

        try {
            List<Address> addresses = geoUtil.getFromLocation(lat, lon, MAX_RESULTS);

            for (Address addy : addresses ) {
                if (addy.getLocality().toLowerCase() == "new york")
                    return new NycLocalOfficialsApi();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
