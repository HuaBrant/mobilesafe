package com.zwh.mobilesafe;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zwh.mobilesafe.service.IService;
import com.zwh.mobilesafe.service.WatchDogService;

/**
 * Created by zwh on 2017/11/15 0015.
 */

public class EntryPwdActivity extends Activity {
    /**
     * Large Text
     */
    private TextView tvEnterpwdName;
    private ImageView ivEnterpwdIcon;
    private EditText etPassword;
    /**
     * 确定
     */
    private Button button1;
    private IService iService;
    private MyConn conn;
    private String packname;
    private Intent serverIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_pwd);
        initView();
        Intent intent =getIntent();
        packname =intent.getStringExtra("packkname");
        serverIntent =new Intent(this,WatchDogService.class);
        conn =new MyConn();
        bindService(serverIntent,conn,BIND_AUTO_CREATE);
        try {
            PackageInfo info =getPackageManager()
                    .getPackageInfo(packname,0);
            tvEnterpwdName.setText(info.applicationInfo
                    .loadLabel(getPackageManager()));
            ivEnterpwdIcon.setImageDrawable(info.applicationInfo
                    .loadIcon(getPackageManager()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void enterPassword(View view){
        //获取到输入框中的密码，并将密码前后的空格清除掉。
        String pwd = etPassword.getText().toString().trim();
        //判断输入的密码是否为空
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return ;
        }
        //判断密码是否为123（正确密码，没有提供设置密码的界面，这里简单的处理一下）。
        if("123".equals(pwd)){
            //通知看门狗 临时的停止对 packname的保护
            iService.callTempStopProtect(packname);
			/*Intent intent = new Intent();
			intent.setAction("cn.itcast.mobilesafe.stopprotect");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);*/
            finish();

        }else{
            Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
            return ;
        }
    }

    /**
     * 当进入当前的界面后，屏蔽掉Back键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_DOWN&&event.getKeyCode()== KeyEvent.KEYCODE_BACK){
            return true;//消费掉当前的Back键
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    private void initView() {
        tvEnterpwdName = (TextView) findViewById(R.id.tv_enterpwd_name);
        ivEnterpwdIcon = (ImageView) findViewById(R.id.iv_enterpwd_icon);
        etPassword = (EditText) findViewById(R.id.et_password);
        button1 = (Button) findViewById(R.id.button1);
    }


    private class MyConn implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iService =(IService) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
}
