package com.mobilonix.voices.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;

import com.mobilonix.voices.VoicesApplication;

public class ViewUtil {

    public static LayoutInflater getInflater(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater;
    }

    public static void setViewColor(View view, int colorId) {
        view.setBackgroundColor(ContextCompat
                .getColor(view.getContext(), colorId));
    }

    public static int getResourceColor(int resource) {
        return ContextCompat.getColor(VoicesApplication.getContext(), resource);
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
