package com.zwh.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by zwh on 2017/11/11 0011.
 */

public class AppInfo {
    private String packname;
    private String version;
    private String appname;
    private Drawable appicon;
    private boolean userapp;

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }

    public boolean isUserapp() {
        return userapp;
    }

    public void setUserapp(boolean userapp) {
        this.userapp = userapp;
    }
}
