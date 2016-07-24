package com.mobilonix.voices.base.util;

import android.widget.Toast;

import com.mobilonix.voices.VoicesApplication;

/* Oh yeah, why is that? It's purpose is to expose general utility functions that don't
necessarily fit into any specific category */
public class GeneralUtil {

    public static void toast(String message) {
        Toast.makeText(VoicesApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
