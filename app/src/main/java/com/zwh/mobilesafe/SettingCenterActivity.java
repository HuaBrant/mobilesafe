package com.zwh.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zwh.mobilesafe.service.CallFirewallService;
import com.zwh.mobilesafe.service.ShowCallLocationServer;
import com.zwh.mobilesafe.service.WatchDogService;
import com.zwh.mobilesafe.utils.ServiceStatusUtil;

/**
 * Created by Administrator on 2017/11/11 0011.
 */

public class SettingCenterActivity extends Activity implements View.OnClickListener{
    private SharedPreferences sp;
    /**
     * 自动更新设置
     */
    private TextView tvSettingAutoupdateText;
    /**
     * 自动更新没有开启
     */
    private TextView tvSettingAutoupdateStatus;
    private CheckBox cbSettingAutoupdate;
    /**
     * 来电归属地设置
     */
    private TextView tvSettingShowLocation;
    /**
     * 来电归属地显示没有开启
     */
    private TextView tvSettingShowLocationStatus;
    private CheckBox cbSettingShowLocation;
    private RelativeLayout rlSettingShowLocation;
    /**
     * 来电归属地风格设置
     */
    private TextView tvSettingChangeBg;
    /**
     * 半透明
     */
    private TextView tvSettingShowBg;
    private RelativeLayout rlSettingChangeBg;
    /**
     * 归属地提示框的位置
     */
    private TextView tvSettingChangeLocation;
    private RelativeLayout rlSettingChangeLocation;
    /**
     * 来电黑名单设置
     */
    private TextView tvSettingCallFirewall;
    /**
     * 来电黑名单拦截没有开启
     */
    private TextView tvSettingCallFirewallStatus;
    private CheckBox cbSettingCallFirewall;
    private RelativeLayout rlSettingCallFirewall;
    /**
     * 程序锁设置
     */
    private TextView tvSettingApplock;
    /**
     * 程序锁服务没有开启
     */
    private TextView tvSettingApplockStatus;
    private CheckBox cbSettingApplock;
    private RelativeLayout rlSettingApplock;
    private Intent WatchDogIntent;
    private Intent showLoactionIntent;
    private Intent watchDogIntent;
    private Intent callFirewallIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_center);
        initView();
        sp=getSharedPreferences("config",MODE_PRIVATE);
        boolean autoupdata =sp.getBoolean("autoupdata",true);
        if (autoupdata){
            tvSettingAutoupdateStatus.setText("自动更新已经开启");
        }else {
            tvSettingAutoupdateStatus.setText("自动更新已经关闭");
        }
        cbSettingAutoupdate.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor=sp.edit();
                editor.putBoolean("autoupdata",b);
                editor.commit();
                if (b){
                    tvSettingAutoupdateStatus.setText("自动更新已经开启");
                }else {
                    tvSettingAutoupdateStatus.setText("自动更新已经关闭");
                }
            }
        });
        showLoactionIntent = new Intent(this,ShowCallLocationServer.class);
        callFirewallIntent = new Intent(this,CallFirewallService.class);
        rlSettingShowLocation.setOnClickListener((View.OnClickListener) this);
        rlSettingChangeBg.setOnClickListener((View.OnClickListener) this);
        rlSettingChangeLocation.setOnClickListener((View.OnClickListener) this);
        rlSettingCallFirewall.setOnClickListener((View.OnClickListener) this);
        watchDogIntent = new Intent(this, WatchDogService.class);
        rlSettingApplock.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    protected void onResume() {
        if (ServiceStatusUtil.isServiceRunning(this,
                "com.zwh.mobilesafe.service.CallFirewallService")){
            cbSettingCallFirewall.setChecked(true);
            tvSettingCallFirewallStatus.setText("来电黑名单拦截已经开启");
        }else {
            cbSettingCallFirewall.setChecked(false);
            tvSettingCallFirewallStatus.setText("来电黑名单拦截没有开启");
        }
         if (ServiceStatusUtil.isServiceRunning(this,
                "com.zwh.mobilesafe.service.ShowCallLocationService")){
            cbSettingShowLocation.setChecked(true);
            tvSettingChangeLocation.setText("来电归属地显示已经开启");
        }else {
            cbSettingCallFirewall.setChecked(false);
            tvSettingCallFirewallStatus.setText("来电归属地显示没有开启");
        }
         if (ServiceStatusUtil.isServiceRunning(this,
                "com.zwh.mobilesafe.service.WatchDoaService1")){
            cbSettingCallFirewall.setChecked(true);
            tvSettingCallFirewallStatus.setText("程序锁服务已经开启");
        }else {
            cbSettingApplock.setChecked(false);
            tvSettingApplock.setText("程序锁服务没有开启");
        }

        super.onResume();
    }


    private void initView() {
        tvSettingAutoupdateText = (TextView) findViewById(R.id.tv_setting_autoupdate_text);
        tvSettingAutoupdateStatus = (TextView) findViewById(R.id.tv_setting_autoupdate_status);
        cbSettingAutoupdate = (CheckBox) findViewById(R.id.cb_setting_autoupdate);
        tvSettingShowLocation = (TextView) findViewById(R.id.tv_setting_show_location);
        tvSettingShowLocationStatus = (TextView) findViewById(R.id.tv_setting_show_location_status);
        cbSettingShowLocation = (CheckBox) findViewById(R.id.cb_setting_show_location);
        rlSettingShowLocation = (RelativeLayout) findViewById(R.id.rl_setting_show_location);
        tvSettingChangeBg = (TextView) findViewById(R.id.tv_setting_change_bg);
        tvSettingShowBg = (TextView) findViewById(R.id.tv_setting_show_bg);
        rlSettingChangeBg = (RelativeLayout) findViewById(R.id.rl_setting_change_bg);
        tvSettingChangeLocation = (TextView) findViewById(R.id.tv_setting_change_location);
        rlSettingChangeLocation = (RelativeLayout) findViewById(R.id.rl_setting_change_location);
        tvSettingCallFirewall = (TextView) findViewById(R.id.tv_setting_call_firewall);
        tvSettingCallFirewallStatus = (TextView) findViewById(R.id.tv_setting_call_firewall_status);
        cbSettingCallFirewall = (CheckBox) findViewById(R.id.cb_setting_call_firewall);
        rlSettingCallFirewall = (RelativeLayout) findViewById(R.id.rl_setting_call_firewall);
        tvSettingApplock = (TextView) findViewById(R.id.tv_setting_applock);
        tvSettingApplockStatus = (TextView) findViewById(R.id.tv_setting_applock_status);
        cbSettingApplock = (CheckBox) findViewById(R.id.cb_setting_applock);
        rlSettingApplock = (RelativeLayout) findViewById(R.id.rl_setting_applock);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_setting_show_location:
                if (cbSettingShowLocation.isChecked()){
                    tvSettingShowLocationStatus.setText("来电归属地显示没有开启");
                    stopService(showLoactionIntent);
                    cbSettingShowLocation.setChecked(false);
                }
                else {
                    tvSettingShowLocationStatus.setText("来电归属地显示已经开启");
                    startService(showLoactionIntent);
                    cbSettingShowLocation.setChecked(true);
                }
                break;
            case R.id.rl_setting_change_bg:
                showChooseBbDialog();
                break;
            case R.id.rl_setting_change_location:
                Intent intent =new Intent(this,DragViewActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_setting_call_firewall:
                if (cbSettingCallFirewall.isChecked()){
                    tvSettingCallFirewallStatus.setText("来电归属地显示没有开启");
                    stopService(callFirewallIntent);
                    cbSettingCallFirewall.setChecked(false);
                }
                else {
                    tvSettingShowLocationStatus.setText("来电归属地显示已经开启");
                    startService(callFirewallIntent);
                    cbSettingCallFirewall.setChecked(true);
                }
                break;
            case R.id.rl_setting_applock:
                if (cbSettingApplock.isChecked()){
                    tvSettingAutoupdateStatus.setText("来电归属地显示没有开启");
                    stopService(watchDogIntent);
                    cbSettingApplock.setChecked(false);
                }
                else {
                    tvSettingAutoupdateStatus.setText("来电归属地显示已经开启");
                    startService(watchDogIntent);
                    cbSettingApplock.setChecked(true);
                }
                break;
        }
    }
    private void showChooseBbDialog(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.notification);
        builder.setTitle("归属地提示框风格");
        final String[] items = { "半透明", "活力橙", "卫士蓝", "苹果绿", "金属灰" };
        final int which =sp.getInt("which",0);
        builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor=sp.edit();
                editor.putInt("which",which);
                tvSettingShowBg.setText(items[which]);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}
