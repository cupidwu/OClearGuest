package com.cupid.wifi.util;

/**
 * Created by qiangwu on 13-11-20.
 */
public enum AsynTaskResult {

    SUCCESS(1),FAILED(0);

    private int rCode;
    private AsynTaskResult(int _rCode){
        this.rCode = _rCode;
    }

    @Override
    public String toString() {
        return String.valueOf(this.rCode);
    }

    public int value(){
        return this.rCode;
    }

    public static AsynTaskResult valueOf(int value){
        switch (value){
            case 0:
                return FAILED;
            case 1:
                return SUCCESS;
            default:
                return null;
        }
    }

}
