package com.cupid.wifi.util;

import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by qiangwu on 13-11-20.
 */
public class WifiParserUtil {

    private static final String TAG = "WifiParserUtil";

    public static String getResponseText(HttpEntity entity)throws IOException{

        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
        String line = null;
        StringBuffer text = new StringBuffer();

        while((line=reader.readLine())!=null){
            text.append(line);
        }
        return text.toString();
    }
}
