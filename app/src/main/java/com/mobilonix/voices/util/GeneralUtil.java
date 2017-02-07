package com.mobilonix.voices.util;

import android.widget.Toast;

import com.mobilonix.voices.VoicesApplication;

import java.util.Calendar;

//general utility functions that don't necessarily fit into any specific category
public class GeneralUtil {

    public static void toast(String message) {
        Toast.makeText(VoicesApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static String getTime(){
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);

        return hours+":"+minutes+":"+seconds;
    }

}
