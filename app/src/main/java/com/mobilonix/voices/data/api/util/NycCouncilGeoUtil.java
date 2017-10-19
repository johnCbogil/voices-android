package com.mobilonix.voices.data.api.util;

import android.location.Address;
import android.util.Log;

import com.mobilonix.voices.R;
import com.mobilonix.voices.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

//Converts NYC lat / lon to physical address String and provides address String and mBorough

public class NycCouncilGeoUtil extends VoicesGeoUtil {

    int mBorough;

    private int[] districtReference = new int[]{1,2,5,7,8,16,18,20,21,23,24,33,34,35,36,39,40,41,43,47,49,13,19,11,12,4,42,45,29,30,3,6,50,51,46,48,37,14,9,10,15,17,22,25,26,27,28,38,44,31,32};

    public NycCouncilGeoUtil(double lat, double lon) {
        List<Address> addresses = super.resetLocation(lat,lon);
        parseBorough(addresses);
    }

    //TODO: switch to regex match in order to take advantage of matches() method for multiple terms
    //TODO: this area can be gutted since we're using PIP algorithm rather than checking website

    private void parseBorough(List<Address> addresses) {
        String allAddys = null;

        try {
            allAddys = addresses.toString().toLowerCase();
        } catch (Exception e) {
            //TODO: add proper exception handling
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
            } else if (allAddys.contains("manhattan") || allAddys.contains("new york, ny")) {
                mBorough = 1;
            }
        }
    }

    public int filterDistrict(double lat, double lon) {

        try {
            JSONObject jObj = JsonUtil.getJsonFromResource(R.raw.city_council_districts);

            JSONArray districts = jObj.getJSONArray("features");

            for(int i = 0; i < districts.length(); i++) {

                JSONArray shapes = districts.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");

                for(int j = 0; j < shapes.length(); j++) {


                    JSONArray shape = shapes.getJSONArray(j).getJSONArray(0);

                    double[] lats = new double[shape.length()];
                    double[] lons = new double[shape.length()];

                    for(int k = 0; k < shape.length(); k++) {

                            JSONArray latlon = shape.getJSONArray(k);

                            lats[k] = Double.parseDouble(latlon.get(1).toString());
                            lons[k] = Double.parseDouble(latlon.get(0).toString());
                    }
                    if(isPointInPoly(lats.length,lats,lons,lat,lon)) {
                        return districtReference[i];
                    }
                }
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean isPointInPoly(int numVert, double[] vertX, double[] vertY, double testX, double testY) {

        int i, j;
        boolean c = false;
        
        for (i = 0, j = numVert-1; i < numVert; j = i++) {

            if ( ((vertY[i]>testY) != (vertY[j]>testY)) &&
                (testX < (vertX[j]-vertX[i]) * (testY-vertY[i]) / (vertY[j]-vertY[i]) + vertX[i])) {
                c = !c; 
            }
        }
        return c;
    }

    public String getAddressLine() {
        return  mAddressLine;
    }

    public String getBorough() {
        return Integer.toString(mBorough);
    }
}