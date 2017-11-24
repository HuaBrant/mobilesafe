package com.zwh.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.zwh.mobilesafe.R;
import com.zwh.mobilesafe.domain.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwh on 2017/11/12 0012.
 */

public class ProcessInfoProvider {
    private Context context;
    public ProcessInfoProvider(Context context){
        this.context=context;
    }
    public List<ProcessInfo> getProcessInfos(){
        ActivityManager activityManager =(ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager packageManager =context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos
                =activityManager.getRunningAppProcesses();
        List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
        for (ActivityManager.RunningAppProcessInfo info:runningAppProcessInfos
             ) {
            ProcessInfo processInfo = new ProcessInfo();
            processInfo.setPid(info.pid);
            String packName =info.processName;
            processInfo.setPackname(packName);
            processInfo.setMemsize(activityManager.getProcessMemoryInfo(new
            int[]{info.pid})[0].getTotalPrivateDirty()*1024);
            try {
                ApplicationInfo applicationInfo =packageManager
                        .getApplicationInfo(packName,0);
                if (filterApp(applicationInfo)){
                    processInfo.setUserprocess(true);
                }else {
                    processInfo.setUserprocess(false);
                }
                processInfo.setIcon(applicationInfo.loadIcon(packageManager));
                processInfo.setAppname(applicationInfo.loadLabel(packageManager)
                        .toString());
            } catch (Exception e) {
                e.printStackTrace();
                processInfo.setUserprocess(false);
                processInfo.setAppname(info.processName);
                processInfo.setIcon(context.getResources()
                        .getDrawable(R.mipmap.ic_launcher));
                processInfo.setAppname(packName);
            }
            processInfos.add(processInfo);
            processInfo=null;
        }
        return processInfos;
    }

    private boolean filterApp(ApplicationInfo applicationInfo) {
        if ((applicationInfo.flags& ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)!=0) {
            return true;
        }else if ((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
            return true;
        }
        return false;
    }
}
