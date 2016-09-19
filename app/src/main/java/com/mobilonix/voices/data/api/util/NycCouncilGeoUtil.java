package com.mobilonix.voices.data.api.util;

import android.location.Address;
import android.util.Log;

import com.mobilonix.voices.R;
import com.mobilonix.voices.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 *
 * Converts NYC lat / lon to physical address String and provides address String and mBorough*
 *
 */

public class NycCouncilGeoUtil extends VoicesGeoUtil{

    int mBorough;

    public NycCouncilGeoUtil(double lat, double lon) {
        List<Address> addresses = super.resetLocation(lat,lon);
        parseBorough(addresses);
    }

    //TODO switch to regex match in order to take advantage of matches() method for multiple terms
    private void parseBorough(List<Address> addresses) {
        String allAddys = null;

        try {
            allAddys = addresses.toString().toLowerCase();
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

    public int filterDistrict(double lat, double lon) {
        JSONObject jObj = JsonUtil.getJsonFromResource(R.raw.district_sampling);

        int district=-1;

        try {
            JSONArray map = jObj.getJSONArray("map");

            double shortestDistance = Double.MAX_VALUE;

            for(int i = 0; i < map.length(); i++) {

                double shapeLat = ((JSONObject) map.get(i)).getDouble("lat");
                double shapeLon = ((JSONObject) map.get(i)).getDouble("lon");

                double distance = distanceCalc(shapeLat, shapeLon, lat, lon);
                Log.i("json","distance: " + distance + "sLat: " +shapeLat + "sLon: " + shapeLon + "lat: " +lat + "lon: " + lon);

                if(distance < shortestDistance) {
                    shortestDistance = distance;
                    district = ((JSONObject) map.get(i)).getInt("dist");
                    Log.i("json","district:"  + district + "shortest dist: " + shortestDistance);
                }
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }
        return district+1;
    }

    private static double distanceCalc(double x1, double y1, double x2, double y2) {
        return Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
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
