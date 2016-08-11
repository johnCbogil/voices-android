package com.mobilonix.voices.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * A single purpose tool for registering a single connectivity state change listener.
 */
public class ConnectivityReceiverUtil {

    /* TODO expand this tool to handle mutliple broadcastReceivers and add multiple
        listeners per receiver.
     */

    public static final String TAG = "ConnectivityReceiverUti";

    public static final String CONNECTIVITY_INTENT = "android.net.conn.CONNECTIVITY_CHANGE";

    ConnectivityListener mConnectivityListener;

    BroadcastReceiver broadcastReceiver;

    public interface ConnectivityListener {
        void onConnectivityStateChanged(boolean isConnected, NetworkInfo info);
    }

    public void setConnectivityStateChangeListener(ConnectivityListener connectivityListener){
        mConnectivityListener = connectivityListener;
    }

    public void registerConnectivityReceiver(Context context, ConnectivityListener connectivityListener) {

        if(broadcastReceiver == null) {
            mConnectivityListener = connectivityListener;
            broadcastReceiver = getBroadcastReceiver();
            context.registerReceiver(broadcastReceiver,
                    new IntentFilter(CONNECTIVITY_INTENT));
        }
    }

    public void unregisterConnectivityReceiver(Context context) {

        if(broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean hasData = false;

                Log.i(TAG, "intent.getAction: " + intent.getAction().toString());

                if (!intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) &&
                        !intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) &&
                        !intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                    return;
                }

                ConnectivityManager connManager = (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo info = connManager.getActiveNetworkInfo();

                    if (info != null && info.isConnectedOrConnecting() ) {
                    mConnectivityListener.onConnectivityStateChanged(true, info);
                    Log.d("VoicesMainActivity",
                            "Connected through route: " + info.getTypeName());
                } else {
                    mConnectivityListener.onConnectivityStateChanged(false,info);
                    Log.d(TAG,
                            "Disconnected - Data Unavailable" );
                }
            }
        };
    }
}
