package com.zwh.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.zwh.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

/**
 * Created by zwh on 2017/11/15 0015.
 */

public class CallFirewallService extends Service{
    public static final String TAG="CallFirewallService";
    private TelephonyManager telephonyManager;
    private MyPhoneListener listener;
    private BlackNumberDao dao;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);
        listener = new MyPhoneListener();
        telephonyManager =(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class MyPhoneListener extends PhoneStateListener{

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    int mode=dao.findNumberMode(incomingNumber);
                    if (mode==0||mode==2){
                        Log.i(TAG, "挂断电话 ");
                        endcall(incomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void endcall(String incomingNumber) {
        try {
            //使用反射获取系统的service方法
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null,
                    new Object[] { TELEPHONY_SERVICE });
            //通过aidl实现方法的调用
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();//该方法是一个异步方法，他会新开启一个线程将呼入的号码存入数据库中

            //deleteCallLog(incomingNumber);

            // 注册一个内容观察者 观察uri数据的变化
            getContentResolver().registerContentObserver(
                    CallLog.Calls.CONTENT_URI, true, new MyObserver(new Handler(), incomingNumber));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyObserver extends ContentObserver {
        public MyObserver(Handler handler, String incomingNumber) {
            super(handler);
            deleteCallLog(incomingNumber);
            getContentResolver().unregisterContentObserver(this);
        }
    }

    private void deleteCallLog(String incomingNumber) {
        Uri uri =Uri.parse("content://call_log/calls");
        Cursor cursor =getContentResolver().query(uri, new String[] { "_id" },
                "number=?", new String[] { incomingNumber }, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            getContentResolver().delete(uri, "_id=?", new String[] { id });
        }
        cursor.close();
    }
}
