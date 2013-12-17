package com.cupid.wifi.server;

import android.util.Log;

import com.cupid.wifi.util.WifiParserUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qiangwu on 13-11-20.
 */
public class ServerInfo {

    private static final String TAG = "ServerInfo";

    //public static final String DEFAULT_URL = "http://www.wis-tech.com.cn:8080/plugin/wifikey/";
    public static final String DEFAULT_URL = "http://222.126.233.70:8080/plugin/wifikey/";
    private static String curServerURL = DEFAULT_URL;

    public static void setCurServerURL(String curServerURL) {
        ServerInfo.curServerURL = curServerURL;
    }

    public static String getCurServerURL() {
        return curServerURL;
    }

    public static String getServerPwd() {
        String pwd = "";

        HttpClient hc = new DefaultHttpClient();
        HttpGet hg = new HttpGet(curServerURL + genKeyFileName());
        try {
            HttpResponse hr = hc.execute(hg);

            pwd = WifiParserUtil.getResponseText(hr.getEntity());
            if(pwd.length() > 20){
                //invalidate
                Log.d(TAG,"Error password:" + pwd);
                pwd = "";
            }
            Log.d(TAG, "Exist password:" + pwd);
        } catch (Exception ex) {
            Log.w(TAG, ex.toString());
        }
        return pwd;
    }

    private static String genKeyFileName() {

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

        String keyFile = sf.format(new Date(System.currentTimeMillis())) + ".txt";

        return keyFile;
    }

    /*
     * Upload current day's pwd info to the server
     */
    public static boolean uploadPwd(String newPwd) {
        try {
            HttpClient hc = new DefaultHttpClient();
            HttpGet hg = new HttpGet(curServerURL + "key.php?key=" + newPwd);
            hc.execute(hg);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            return false;
        }
        Log.d(TAG, "Upload WIFI key password successful:" + newPwd);
        return true;
    }
}
