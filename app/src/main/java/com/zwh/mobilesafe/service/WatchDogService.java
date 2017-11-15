package com.zwh.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zwh.mobilesafe.EntryPwdActivity;
import com.zwh.mobilesafe.db.dao.AppLockDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwh on 2017/11/15 0015.
 */

public class WatchDogService extends Service{
    protected static final String TAG="WatchDogService";
    boolean flag;
    private AppLockDao dao;
    private Intent pwdintent;
    private List<String> tempStopProtectPachnames;
    private List<String> lochPacknames;
    private MyObserver observer;
    private LockScreenReceiver receiver;
    private MyBinder binder;
    @Override
    public void onCreate() {
        Uri uri = Uri.parse("content://cn.itcast.applock/");
        observer =new MyObserver(new Handler());
        getContentResolver().registerContentObserver(uri,true,observer);
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        receiver =new LockScreenReceiver();
        registerReceiver(receiver,filter);
        super.onCreate();

        dao =new AppLockDao(this);
        lochPacknames =dao.findAll();
        flag=true;
        tempStopProtectPachnames =new ArrayList<String>();
        pwdintent = new Intent(this,EntryPwdActivity.class);
        pwdintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Thread(){
            @Override
            public void run() {
                while (flag){
                    ActivityManager activityManager =(ActivityManager)
                            getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.RunningTaskInfo taskInfo=
                            activityManager.getRunningTasks(1).get(0);
                    String packname = taskInfo.topActivity.getPackageName();
                    Log.i(TAG, packname);
                    if (tempStopProtectPachnames.contains(packname)){
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    pwdintent.putExtra("packname",packname);
                    if (lochPacknames.contains(packname)){
                        startActivity(pwdintent);
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag=false;
        getContentResolver().unregisterContentObserver(observer);
        observer=null;
        unregisterReceiver(receiver);
        binder=null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        binder =new MyBinder();
        return binder;
    }

    private class MyObserver extends ContentObserver{
        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            lochPacknames =dao.findAll();
            super.onChange(selfChange);
        }
    }

    private class LockScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "锁屏了 ");
            tempStopProtectPachnames.clear();
        }
    }

    private class MyBinder extends Binder implements IService{
        @Override
        public void callTempStopProtect(String packname) {
            tempStopProtect(packname);
        }
    }

    private void tempStopProtect(String packname) {
        tempStopProtectPachnames.add(packname);
    }
}
