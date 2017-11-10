package com.zwh.mobilesafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.zwh.mobilesafe.adapter.MianAdapter;

public class MainActivity extends AppCompatActivity {
    private GridView gv_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gv_main = (GridView) findViewById(R.id.gv_main);
        gv_main.setAdapter(new MianAdapter(this));
        gv_main.setOnClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0: //手机防盗
                        //跳转到”手机防盗“对应的Activity界面
                        Intent lostprotectedIntent = new Intent(MainActivity.this,LostProtectedActivity.class);
                        startActivity(lostprotectedIntent);
                        break;
                    case 1: //通讯卫士
                        Intent callSmsIntent = new Intent(MainActivity.this,CallSmsSafeActivity.class);
                        startActivity(callSmsIntent);
                        break;
                    case 2: //程序管理
                        Intent appManagerIntent = new Intent(MainActivity.this,AppManagerActivity.class);
                        startActivity(appManagerIntent);
                    case 3: //进程管理
                        Intent taskManagerIntent = new Intent(MainActivity.this,TaskManagerActivity.class);
                        startActivity(taskManagerIntent);
                        break;
                    case 4: //流量管理
                        Intent trafficInfoIntent = new Intent(MainActivity.this,TrafficInfoActivity.class);
                        startActivity(trafficInfoIntent);
                        break;
                    case 5: //手机杀毒
                        Intent antiVirusIntent = new Intent(MainActivity.this,AntiVirusActivity.class);
                        startActivity(antiVirusIntent);
                        break;
                    case 6: //系统优化
                        Intent cleanCacheIntent = new Intent(MainActivity.this,CleanCacheActivity.class);
                        startActivity(cleanCacheIntent);
                        break;
                    case 7://高级工具
                        Intent atoolsIntent = new Intent(MainActivity.this,AtoolsActivity.class);
                        startActivity(atoolsIntent);
                        break;
                    case 8://设置中心
                        //跳转到”设置中心“对应的Activity界面
                        Intent settingIntent = new Intent(MainActivity.this,SettingCenterActivity.class);
                        startActivity(settingIntent);
                        break;
                }
            }
        });
    }
}
