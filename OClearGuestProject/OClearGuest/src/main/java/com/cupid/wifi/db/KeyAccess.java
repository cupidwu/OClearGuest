package com.cupid.wifi.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.cupid.wifi.server.ServerInfo;
import com.cupid.wifi.util.DateUtil;

/**
 * Created by qiangwu on 13-11-20.
 */
public class KeyAccess {

    private static final String PREFERS_NAME = "com.cupid.wifi";

    public static final String DATE_KEY="date";
    public static final String PWD_KEY="pwd";

    public static final String SERVER_URL="server";


    public static void storeNewKey(Context c, String pwd){
        //store the date..
        SharedPreferences settings = c.getSharedPreferences(PREFERS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(DATE_KEY, DateUtil.getCurrentDate());
        editor.putString(PWD_KEY, pwd);
        editor.commit();
    }

    public static String getKey(Context c){
        SharedPreferences settings = c.getSharedPreferences(PREFERS_NAME, 0);

        if(settings.getString(DATE_KEY,"").equals(DateUtil.getCurrentDate())){
            return settings.getString(PWD_KEY, "");
        }else{
            return "";
        }
    }

    public static void storeNewServerURL(Context c, String newUrl){

        SharedPreferences settings = c.getSharedPreferences(PREFERS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(SERVER_URL, newUrl);
        editor.commit();
    }

    public static String getServerURL(Context c){
        SharedPreferences settings = c.getSharedPreferences(PREFERS_NAME, 0);

        return settings.getString(SERVER_URL, ServerInfo.DEFAULT_URL);
    }



}
