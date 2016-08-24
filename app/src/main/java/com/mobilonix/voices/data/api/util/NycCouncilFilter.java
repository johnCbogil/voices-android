package com.mobilonix.voices.data.api.util;

import android.content.Context;
import android.util.Log;

import com.mobilonix.voices.R;
import com.mobilonix.voices.data.api.engines.NycCouncilApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NycCouncilFilter {

    public static int filterDistrict(Context context, double lat, double lon) {
        JSONObject jObj = NycCouncilApi.getJsonFromResource(context,R.raw.json_proper);

        int district=-1;

        try {
            JSONArray map = jObj.getJSONArray("map");

            double shortestDistance = Double.MAX_VALUE;

            for(int i = 0; i < map.length(); i++) {

                //TODO these are backwards in JSON!!!! FIX JSON!!!
                double shapeLat = ((JSONObject) map.get(i)).getDouble("lon");
                double shapeLon = ((JSONObject) map.get(i)).getDouble("lat");

                double distance = distanceCalc(shapeLat, shapeLon, lat, lon);
                Log.i("json","distance: " + distance + "sLat: " +shapeLat + "sLon: " + shapeLon + "lat: " +lat + "lon: " + lon);

                if(distance < shortestDistance) {
                    shortestDistance = distance;
                    Log.i("json","shortest dist: " + shortestDistance);
                    district = ((JSONObject) map.get(i)).getInt("dist");
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
}
