package com.mobilonix.voices.util;

import android.content.Context;
import android.view.LayoutInflater;

public class ViewUtil {

    public static LayoutInflater getInflater(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater;
    }

}
