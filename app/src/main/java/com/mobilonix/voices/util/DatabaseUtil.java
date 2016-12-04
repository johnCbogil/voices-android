package com.mobilonix.voices.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.representatives.model.Representative;

import java.util.ArrayList;
import java.util.HashSet;

public class DatabaseUtil {

    public static void saveRepresentatives(String repsLevel,
                                           ArrayList<Representative> repsList){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(VoicesApplication.getContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        HashSet<String> repsHashSet = new HashSet<String>();
        for(int i = 0; i < repsList.size(); i++) {

            Representative representative = repsList.get(i);

            RepresentativesManager.INSTANCE.getBitmapFromMemCache(i + "");

            String repJson = gson.toJson(representative);
            repsHashSet.add(repJson);
        }
        editor.putStringSet(repsLevel,repsHashSet);
        editor.commit();
    }

    public static ArrayList<Representative> fetchRepresentatives(String repsLevel){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(VoicesApplication.getContext());
        HashSet<String> repsHashSet = (HashSet<String>)prefs.getStringSet(repsLevel, new HashSet<String>());
        Gson gson = new Gson();
        ArrayList<Representative> arrayList = new ArrayList<Representative>();
        for(String string : repsHashSet){
            arrayList.add(gson.fromJson(string, Representative.class));
        }
        return arrayList;
    }

    public boolean isOnline() {
        try
        {
            ConnectivityManager cm = (ConnectivityManager)
                    VoicesApplication.getContext()
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
