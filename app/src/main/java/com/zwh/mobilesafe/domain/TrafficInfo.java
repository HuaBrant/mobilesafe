package com.zwh.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by zwh on 2017/11/13 0013.
 */

public class TrafficInfo {
    private String packname;
    private String appname;
    private long updata;
    private long downdata;
    private Drawable icon;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public long getUpdata() {
        return updata;
    }

    public void setUpdata(long updata) {
        this.updata = updata;
    }

    public long getDowndata() {
        return downdata;
    }

    public void setDowndata(long downdata) {
        this.downdata = downdata;
    }
}
