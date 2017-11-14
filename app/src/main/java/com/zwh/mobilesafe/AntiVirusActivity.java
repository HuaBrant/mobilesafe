package com.zwh.mobilesafe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zwh.mobilesafe.db.dao.AntiVirusDao;
import com.zwh.mobilesafe.utils.Md5Encoder;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/11 0011.
 */

public class AntiVirusActivity extends Activity{
    protected static final int SCAN_NOT_VIRUS = 90;
    protected static final int FIND_VIRUS = 91;
    protected static final int  SCAN_FINISH= 92;
    //the radar image's needle when kill virus;
    private ImageView iv_scan;
    //app PackageManager pm;
    private PackageManager pm;
    //VirusDao
    private AntiVirusDao dao;
    private ProgressBar progressBar;
    //the count if found virus
    private TextView tv_scan_status;
    //show files which be scanned info;
    private LinearLayout ll_scan_status;
    private List<PackageInfo> virusPackInfos;
    //rotate animation
    RotateAnimation rotateAnimation;
    private Map<String,String> virusMap;
    @SuppressLint("HandlerLeak")
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            PackageInfo info=(PackageInfo) msg.obj;
            switch (msg.what){
                case SCAN_NOT_VIRUS:
                    TextView tv=new TextView(getApplicationContext());
                    tv.setText("扫描"+info.applicationInfo.loadLabel(pm)+"安全");
                    ll_scan_status.addView(tv,0);
                    break;
                case FIND_VIRUS:
                    virusPackInfos.add(info);
                    break;
                case SCAN_FINISH:
                    iv_scan.clearAnimation();
                    if (virusPackInfos.size()==0){
                        Toast.makeText(getApplicationContext(), "扫描完毕,你的手机很安全", Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anti_virus);
        pm=getPackageManager();
        dao =new AntiVirusDao(this);
        tv_scan_status =findViewById(R.id.tv_scan_status2);
        iv_scan =findViewById(R.id.iv_scan);
        rotateAnimation =new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,1.0f,
                Animation.RELATIVE_TO_SELF,1.0f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        ll_scan_status=findViewById(R.id.ll_scan_status);
        progressBar = findViewById(R.id.progressBar1);
    }
    public void kill(View view){
        rotateAnimation.reset();
        iv_scan.startAnimation(rotateAnimation);
        new Thread(){
            @Override
            public void run() {
                List<PackageInfo> packageInfos =pm
                        .getInstalledPackages(PackageManager.GET_SIGNATURES);
                progressBar.setMax(packageInfos.size());
                int count=0;
                for (PackageInfo info:packageInfos){
                    String md5= Md5Encoder.encoder(info.signatures[0]
                    .toCharsString());
                    String result=dao.getVirusInfo(md5);
                    if (result == null) {
                        Message msg = Message.obtain();
                        msg.what = SCAN_NOT_VIRUS;
                        msg.obj = info;
                        handler.sendMessage(msg);
                    } else {//当前遍历到的应用属于病毒
                        Message msg = Message.obtain();
                        msg.what = FIND_VIRUS;
                        msg.obj = info;
                        handler.sendMessage(msg);
                    }
                    count++;
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.setProgress(count);
                }
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }
    public void clean(View v) {
        // 判断病毒集合的大小
        if (virusPackInfos.size() > 0) {
            for (PackageInfo pinfo : virusPackInfos) {
                // 卸载应用程序
                String packname = pinfo.packageName;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DEFAULT);
                intent.setData(Uri.parse("package:" + packname));
                startActivity(intent);
            }
        }else{
            return;
        }
    }
}
