package com.cupid.wifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cupid.wifi.db.KeyAccess;
import com.cupid.wifi.model.WebLoginInfo;
import com.cupid.wifi.server.ServerInfo;
import com.cupid.wifi.util.NetworkUtil;

/**
 * Created by qiangwu on 13-11-21.
 */
public class WifiReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiReceiver";

    private Context mContext;

    private Handler iHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle b= msg.getData();

            switch (MsgType.valueOf(b.getInt("type"))){
                case GETKEY:
                    //get wifi key from server
                    String keyValue = b.getString("value");
                    if(keyValue.length() == 0){
                        Toast.makeText(mContext,"抱歉，你是今天第一位，KEY还没有",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(mContext,"获取当天WIFI Key："+keyValue,Toast.LENGTH_LONG).show();
                    }

                    break;
                case LOGIN_SUCCESS:
                    Toast.makeText(mContext,"自动ClearGuest登陆成功",Toast.LENGTH_LONG).show();
                    break;
                case LOGIN_FAILED:
                    Toast.makeText(mContext,"自动ClearGuest登陆失败",Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    public void onReceive(Context context, Intent intent) {

        final Context fContext = context;
        mContext = context;

        if (KeyAccess.getKey(context).length() == 0) {
            if (NetworkUtil.isNetworkConnected(context)) {
                Toast.makeText(context,"尝试获取WIFI KEY中...",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ServerInfo.setCurServerURL(KeyAccess.getServerURL(fContext));
                        String existKey = ServerInfo.getServerPwd();

                        Log.d(TAG, "Server exist key:" + existKey);
                        sendMsg(MsgType.GETKEY,existKey);

                        if(existKey.length()>0){
                            KeyAccess.storeNewKey(fContext, existKey);
                        }
                    }
                }).start();
            }
        }else if (NetworkUtil.isConnectClearGuest(context)) {
            Toast.makeText(context,"自动登陆ClearGuest中...",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean result = WebLoginInfo.loginProcess(KeyAccess.getKey(fContext));
                    if(result){
                        sendMsg(MsgType.LOGIN_SUCCESS,"");
                    }else{
                        sendMsg(MsgType.LOGIN_FAILED,"");
                    }
                    Log.d(TAG, "Login Result:" + result);
                }
            }).start();
        }
    }

    private void sendMsg(MsgType mt, String value){
        Message msg = iHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("type",mt.value());
        b.putString("value",value);
        msg.setData(b);
        msg.sendToTarget();
    }

    private enum MsgType{
        GETKEY(0),LOGIN_SUCCESS(1),LOGIN_FAILED(2);

        private int type;
        private MsgType(int _type){
            this.type = _type;
        }

        @Override
        public String toString() {
            return String.valueOf(this.type);
        }

        public int value(){
            return this.type;
        }

        public static MsgType valueOf(int value){
            switch (value){
                case 0:
                    return GETKEY;
                case 1:
                    return LOGIN_SUCCESS;
                case 2:
                    return LOGIN_FAILED;
                default:
                    return null;
            }
        }
    }
}
