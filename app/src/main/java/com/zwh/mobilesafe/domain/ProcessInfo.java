package com.zwh.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by zwh on 2017/11/12 0012.
 */

public class ProcessInfo {
    //app packName
    private String packname;
    private String appname;
    private Drawable icon;
    private long memsize;
    private boolean userprocess;
    private int pid;
    private boolean checked;

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

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getMemsize() {
        return memsize;
    }

    public void setMemsize(long memsize) {
        this.memsize = memsize;
    }

    public boolean isUserprocess() {
        return userprocess;
    }

    public void setUserprocess(boolean userprocess) {
        this.userprocess = userprocess;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked(){
        return checked;

    }
}
