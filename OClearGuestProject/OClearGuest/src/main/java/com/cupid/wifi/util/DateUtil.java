package com.cupid.wifi.util;

        import java.text.SimpleDateFormat;
        import java.util.Date;

/**
 * Created by qiangwu on 13-11-20.
 */
public class DateUtil {

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    public static String convertPropDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }
}
