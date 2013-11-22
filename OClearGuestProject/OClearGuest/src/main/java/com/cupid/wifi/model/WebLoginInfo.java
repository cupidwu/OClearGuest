package com.cupid.wifi.model;

import android.util.Log;

import com.cupid.wifi.db.KeyAccess;
import com.cupid.wifi.util.AsynTaskResult;
import com.cupid.wifi.util.WifiParserUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by qiangwu on 13-11-20.
 */
public class WebLoginInfo {

    public static final String LOGIN_URL="https://webauth-redirect.oracle.com/login.html";

    private static final int REQUEST_TIMEOUT = 10*1000;//设置请求超时10秒钟
    private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟

    private static final String TAG="WebLoginInfo";

    public static void fillLoginInfo(String key, HttpPost httpPost) throws UnsupportedEncodingException{
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("buttonClicked","4"));
        nvps.add(new BasicNameValuePair("redirect_url","www.baidu.com"));
        nvps.add(new BasicNameValuePair("err_flag", "0"));
        nvps.add(new BasicNameValuePair("username", "guest"));
        nvps.add(new BasicNameValuePair("password", key));

        Log.d(TAG,nvps.toString());

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
    }

    public static boolean loginProcess(String key){

        //timeout setting
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
        HttpClient hc = new DefaultHttpClient(httpParams);

        try{
            HttpPost hp = new HttpPost(WebLoginInfo.LOGIN_URL);
            WebLoginInfo.fillLoginInfo(key,hp);

            HttpResponse hr = hc.execute(hp);
            if(hr.getStatusLine().getStatusCode() == 200){
                Log.d(TAG,"Login Success...");
                return true;
            }else{
                Log.d(TAG, WifiParserUtil.getResponseText(hr.getEntity()));
            }
        }catch (UnsupportedEncodingException uee){
            Log.e(TAG, uee.toString());
        }catch(IOException ioe){
            Log.e(TAG, ioe.toString());
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
        return false;
    }
}
