package com.zwh.mobilesafe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zwh.mobilesafe.utils.Md5Encoder;

/**
 * Created by Administrator on 2017/11/10 0010.
 */
 public class LostProtectedActivity extends Activity implements View.OnClickListener {
    private static final String TAG="LostProtectedActivity";
    private SharedPreferences sp;
    private EditText et_first_dialog_pwd;
    private EditText et_first_dialog_pwd_confirm;
    private Button bt_first_dialog_ok;
    private Button bt_first_dialog_cancle;
    //第二次进入”手机防盗“界面时的界面控件对象
    private EditText et_normal_dialog_pwd;
    private Button bt_normal_dialog_ok;
    private Button bt_normal_dialog_cancle;
    //设置向导结束后的结果界面中的控件
    private TextView tv_lost_protect_number;//绑定的安全号码
    private RelativeLayout rl_lost_protect_setting;//防盗保护设置是否开启所在的父控件，获取该控件是要为该控件设置点击事件（点击该控件中的任意一个控件都会响应点击事件）
    private CheckBox cb_lost_protect_setting;//防盗保护是否开启
    private TextView tv_lost_protect_reentry_setup;//该控件的点击事件执行：重新进入设置向导界面
    private Dialog dialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        if (isSetupPwd()){
            showNormalEntryDialog();
        }else {
            showFirstEntryDialog();
        }
    }

    private void showFirstEntryDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.first_entry_dialog,null);
        //查找view对象中的各个控件
        et_first_dialog_pwd = (EditText) view
                .findViewById(R.id.et_first_dialog_pwd);
        et_first_dialog_pwd_confirm = (EditText) view
                .findViewById(R.id.et_first_dialog_pwd_confirm);
        bt_first_dialog_ok = (Button) view
                .findViewById(R.id.bt_first_dialog_ok);
        bt_first_dialog_cancle = (Button) view
                .findViewById(R.id.bt_first_dialog_cancle);
        //分别为“取消”、“确定”按钮设置一个监听器
        bt_first_dialog_cancle.setOnClickListener(this);
        bt_first_dialog_ok.setOnClickListener(this);
        //将上面的View对象添加到对话框上
        builder.setView(view);
        //获取到对话框对象
        dialog = builder.create();
        //显示出对话框
        dialog.show();
    }

    private void showNormalEntryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            //当点击“取消”按钮时，直接结束掉当前的LostProtectedActivity，程序会进入到主界面
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        //通过View对象的inflate(Context context, int resource, ViewGroup root)对象将非第一次进入“手机防盗”要弹出的窗体对话框的布局文件转换为一个View对象
        View view = View.inflate(this, R.layout.normal_entry_dialog, null);
        //查找view对象中的各个控件
        et_normal_dialog_pwd = (EditText) view
                .findViewById(R.id.et_normal_dialog_pwd);
        bt_normal_dialog_ok = (Button) view
                .findViewById(R.id.bt_normal_dialog_ok);
        bt_normal_dialog_cancle = (Button) view
                .findViewById(R.id.bt_normal_dialog_cancle);
        //分别为“取消”、“确定”按钮设置一个监听器
        bt_normal_dialog_cancle.setOnClickListener(this);
        bt_normal_dialog_ok.setOnClickListener(this);
        //将上面的View对象添加到对话框上
        builder.setView(view);
        //获取到对话框对象
        dialog = builder.create();
        //显示出对话框
        dialog.show();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_first_dialog_cancle:
                dialog.cancel();
                finish();
                break;
            case R.id.bt_first_dialog_ok:
                //获取到两个EditText中的输入的密码，并将EditText前后的空格给去除掉
                String pwd = et_first_dialog_pwd.getText().toString().trim();
                String pwd_confirm=et_first_dialog_pwd_confirm.getText().
                        toString().trim();
                if (TextUtils.isEmpty(pwd_confirm)||TextUtils.isEmpty(pwd)){
                    Toast.makeText(this,"密码不能为空",1).show();
                    return;
                }
                if (pwd.equals(pwd_confirm)){
                    SharedPreferences.Editor  editor = sp.edit();
                    editor.putString("password", Md5Encoder.encoder(pwd));
                    editor.commit();
                    dialog.dismiss();
                    finish();
                }else {
                    Toast.makeText(this, "两次密码不相同", 1).show();
                    return;
                }
                break;
            case R.id.bt_normal_dialog_cancle:
                dialog.cancel();;
                finish();
                break;
            case R.id.bt_normal_dialog_ok:
                String userentrypwd = et_normal_dialog_pwd.getText().toString().trim();
                if(TextUtils.isEmpty(userentrypwd)){
                    Toast.makeText(this,"密码不能为空",1).show();
                    return;
                }
                String savepwd =sp.getString("password","");
                //因为我们在设置密码后，存入的是加密后的密码，所以当我们将输入的密码与设置的密码比较时需要将输入的密码先加密
                if (savepwd.equals(Md5Encoder.encoder(userentrypwd))){
                    Toast.makeText(this,"密码正确进入界面",1).show();
                    dialog.dismiss();
                    if(isSetupAlread()){
                        //进入到完成设置向导后的界面
                        Log.i(TAG,"进入到完成设置向导后的界面");
                        setContentView(R.layout.lost_protected);
                        tv_lost_protect_number = (TextView)findViewById(R.id.tv_lost_protect_number);
                        String safenumber = sp.getString("safenumber","");
                        tv_lost_protect_number.setText(safenumber);
                        //防盗保护设置是否开启所在的父控件，获取该控件是要为该控件设置点击事件（点击该控件中的任意一个控件都会响应点击事件）
                        rl_lost_protect_setting = (RelativeLayout)findViewById(R.id.rl_lost_protect_setting);
                        //防盗保护是否开启
                        cb_lost_protect_setting = (CheckBox)findViewById(R.id.cb_lost_protect_setting);
                        boolean protecting = sp.getBoolean("protecting",false);
                        cb_lost_protect_setting.setChecked(protecting);
                        if (protecting){
                            cb_lost_protect_setting.setText("防盗保护已经开启");
                        }else{
                            cb_lost_protect_setting.setText("防盗保护没有开启");
                        }
                        //该控件的点击事件执行：重新进入设置向导界面
                        tv_lost_protect_reentry_setup = (TextView)findViewById(R.id.tv_lost_protect_reentry_setup);

                        rl_lost_protect_setting.setOnClickListener(this);
                        tv_lost_protect_reentry_setup.setOnClickListener(this);
                    }else {
                        //进入设置向导界面
                        Log.i(TAG,"进入到设置向导界面");
                        Intent intent = new Intent(this,Setup1Activity.class);
                        //执行该方法的原因在于：当用户完成设置向导后按back键时，避免出现之前的界面，增强用户体验效果
                        finish();
                        startActivity(intent);
                    }
                    return;
                }else {
                    Toast.makeText(this,"密码不正确",1).show();
                }
                break;
            case R.id.tv_lost_protect_reentry_setup:
                Intent reentryIntent = new Intent(this,Setup1Activity.class);
                startActivity(reentryIntent);
                finish();
                break;
            case R.id.rl_lost_protect_setting:
                SharedPreferences.Editor editor =sp.edit();
                if(cb_lost_protect_setting.isChecked()){
                    cb_lost_protect_setting.setChecked(false);
                    cb_lost_protect_setting.setText("防盗保护没有开启");
                    editor.putBoolean("protecting", false);
                }else {
                    cb_lost_protect_setting.setChecked(true);
                    cb_lost_protect_setting.setText("防盗保护已经开启");
                    editor.putBoolean("protecting", true);
                }
                editor.commit();
                break;
        }

    }

    public boolean isSetupPwd() {
        String savedpwd=sp.getString("password","");
        if(TextUtils.isEmpty(savedpwd)){
            return false;
        }else {
            return true;
        }
    }

    public boolean isSetupAlread() {
        return sp.getBoolean("issetup",false);
    }
    /**
     * 当一个菜单中的Item被选中时，框架回调该方法，并将所单击的Item传入
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //上面定义的id为1
        if (item.getItemId() == 1) {
            //获取一个窗体构造器
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //创建一个文本输入框
            final EditText et = new EditText(this);
            //设置文本输入框中提示的内容，当点击文本输入框时，该内容会自动消失
            et.setHint("请输入新的标题名称,可以留空");
            //将文本输入框添加到窗体对话框上
            builder.setView(et);
            //为窗体对话框添加一个“确定”按钮
            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        //当点击“确定”按钮时要执行的回调方法
                        public void onClick(DialogInterface dialog, int which) {
                            //获取文本输入框中的内容，并将文本前后的空格去除掉
                            String newname = et.getText().toString().trim();
                            //获取sp对应的编辑器
                            SharedPreferences.Editor editor = sp.edit();
                            //将修改后的名称保存到sp中，此时数据还只在缓存中
                            editor.putString("newname", newname);
                            //数据真正的被保存到sp对应的文件中
                            editor.commit();
                        }
                    });
            //创建并显示出窗体对话框
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}
