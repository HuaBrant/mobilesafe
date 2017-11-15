package com.zwh.mobilesafe.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zwh.mobilesafe.R;
import com.zwh.mobilesafe.db.dao.NumberAddressDao;

/**
 * Created by zwh on 2017/11/15 0015.
 */

public class ShowCallLocationServer extends Service{
    private TelephonyManager telephonyManager;
    private MyPhoneListener listener;
    private WindowManager windowManager;
    private SharedPreferences sp;
    private static final  int[] bgs = {R.mipmap.call_blue,R.mipmap.call_orange,
            R.mipmap.call_blue,R.mipmap.call_green, R.mipmap.call_gray};

    public IBinder onBind(Intent intent){
        return null;
    }

    @SuppressLint("ServiceCast")
    @Override
    public void onCreate() {
        super.onCreate();
        sp =getSharedPreferences("config",MODE_PRIVATE);
        listener = new MyPhoneListener();
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        telephonyManager.listen(listener,PhoneStateListener.LISTEN_NONE);
    }

    private class MyPhoneListener extends PhoneStateListener{
        private View view;
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    String address = NumberAddressDao.getAddress(incomingNumber);
                    view =View.inflate(getApplicationContext(),R.layout.show_address,null);
                    LinearLayout linearLayout=(LinearLayout) view
                            .findViewById(R.id.ll_show_address);
                    int which =sp.getInt("which",0);
                    linearLayout.setBackgroundResource(bgs[which]);
                    TextView textView =(TextView) view.findViewById(R.id.tv_show_address);
                    textView.setText(address);
                    final WindowManager.LayoutParams params =new WindowManager.LayoutParams();
                    params.gravity = Gravity.LEFT|Gravity.TOP;
                    params.x=sp.getInt("lastx",0);
                    params.y=sp.getInt("lasty",0);
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.flags =WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
                            |WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    params.format = PixelFormat.TRANSLUCENT;
                    params.type = WindowManager.LayoutParams.TYPE_TOAST;
                    windowManager.addView(view,params);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (view!=null){
                        windowManager.removeView(view);
                        view=null;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;

            }
            super.onCallStateChanged(state,incomingNumber);
        }
    }
}
