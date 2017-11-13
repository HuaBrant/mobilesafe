package com.zwh.mobilesafe.engine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.zwh.mobilesafe.domain.TrafficInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwh on 2017/11/13 0013.
 */

public class TrafficInfoProvider {
    private PackageManager packageManager;
    private Context context;

    public TrafficInfoProvider( Context context) {
        this.packageManager = context.getPackageManager();
        this.context = context;
    }
    public List<TrafficInfo> getTrafficInfos(){
        List<PackageInfo> packageInfos=packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        List<TrafficInfo> trafficInfos = new ArrayList<TrafficInfo>();
        for (PackageInfo info: packageInfos) {
            String [] permissions = info.requestedPermissions;
            for (String permission : permissions){
                if ("android.permission.INTERNET".equals(permission)){
                    TrafficInfo trafficInfo =new TrafficInfo();
                    trafficInfo.setPackname(info.packageName);
                    trafficInfo.setAppname(info.applicationInfo
                            .loadLabel(packageManager).toString());
                    trafficInfo.setIcon(info.applicationInfo
                            .loadIcon(packageManager));
                    int id=info.applicationInfo.uid;
                    trafficInfo.setDowndata(TrafficStats.getUidRxBytes(id));
                    trafficInfo.setUpdata(TrafficStats.getUidTxBytes(id));
                    trafficInfos.add(trafficInfo);
                    trafficInfo=null;
                    break;
                }
            }
        }
        return trafficInfos;
    }
}
