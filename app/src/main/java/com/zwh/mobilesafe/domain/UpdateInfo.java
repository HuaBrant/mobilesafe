package com.zwh.mobilesafe.domain;

/**
 * Created by zwh on 2017/11/15 0015.
 */

public class UpdateInfo {
    private String version;
    private String description;
    private String apkurl;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApkurl() {
        return apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }
}
