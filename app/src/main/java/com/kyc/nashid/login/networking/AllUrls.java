package com.kyc.nashid.login.networking;

public class AllUrls {

    static {
        System.loadLibrary("main-native-lib");
    }

    public native String loginDevUrl();
    public native String loginProUrl();
    public native String mainDevUrl();
    public native String mainProUrl();
    public String getDevLoginURL(){
        return loginDevUrl();
    }
    public String getProdLoginURL(){
        return loginProUrl();
    }
    public String getDevMainURL(){
        return mainDevUrl();
    }
    public String getProdMainURL(){
        return mainProUrl();
    }

}
