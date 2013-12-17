package com.cupid.wifi.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by qiangwu on 13-11-21.
 */
public class NetworkUtil {

    //clear-guest
    public static final String CLEARGUEST_SSID = "clear-guest";

    private static final String TAG="NetworkUtil";

    public static boolean isNetworkConnected(Context c){
        if(c!=null){
            ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo ni = cm.getActiveNetworkInfo();

            if(ni != null){
                return ni.isAvailable();
            }
        }
        return false;
    }

    public static boolean isMobileConnected(Context c){
        if(c!=null){
            ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if(ni != null){
                return ni.isAvailable();
            }
        }
        return false;
    }

    public static boolean isConnectClearGuest(Context c){

        if(c!=null){
            ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (ni.isConnected()) {
                final WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
                final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo != null && connectionInfo.getSSID().length() > 0
                        && connectionInfo.getSSID().equalsIgnoreCase(CLEARGUEST_SSID)) {
                    Log.d(TAG, "Connect to clear guest wifi...");
                    return true;
                }
            }
        }
        return false;
    }
}
