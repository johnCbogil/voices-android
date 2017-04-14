package com.mobilonix.voices.util;

import android.content.Context;

public class ViewUtil {
    public static int convertDpToPixel(int dpPadding, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pxPadding = (int) (dpPadding * scale + 0.5f);
        return pxPadding;
    }
}
