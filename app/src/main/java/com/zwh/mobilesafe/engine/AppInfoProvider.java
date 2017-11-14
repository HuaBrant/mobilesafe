package com.zwh.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zwh.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwh on 2017/11/12 0012.
 */

public class AppInfoProvider {
    private PackageManager pm;
    public AppInfoProvider(Context context){
        pm =context.getPackageManager();
    }
    /**
     * get all appInfoS
     */
    public List<AppInfo> getInstalledApps(){
        //return all app packageInfoS ;param PackageManager.GET_UNINSTALLED_PACKAGES :uninstall app but not clean info
        List<PackageInfo> packageInfos =pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        List<AppInfo> appinfos = new ArrayList<AppInfo>();
        for (PackageInfo info :packageInfos){
            AppInfo appInfo = new AppInfo();
            appInfo.setPackname(info.packageName);
            appInfo.setAppname(info.applicationInfo.loadLabel(pm).toString());
            appInfo.setAppicon(info.applicationInfo.loadIcon(pm));
            appInfo.setVersion(info.versionName);
            appInfo.setUserapp(filterApp(info.applicationInfo));
            appinfos.add(appInfo);
            appInfo =null;
        }
        return appinfos;
    }

    /**
     * Third-party app filter
     * @param applicationInfo
     * @return
     */
    private boolean filterApp(ApplicationInfo applicationInfo) {
        //this app flag && system app flag
        if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)!=0){
            return true;
        }else if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) ==0){
            return true;
        }
        return false;
    }
}
