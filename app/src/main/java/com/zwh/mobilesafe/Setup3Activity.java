package com.zwh.mobilesafe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/11/10 0010.
 */

class Setup3Activity extends Activity{
    private EditText et_setup3_number;//设置绑定的安全号码
    private SharedPreferences sp;//用于存储安全号码及安全号码的回显
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup3);
        et_setup3_number = (EditText) findViewById(R.id.et_setup3_number);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        //数据的回显。如果没有保存过安全号码，回显的是空
        String number = sp.getString("safemuber", "");
        et_setup3_number.setText(number);
    }
    /**
     * 在第三个界面的“选择联系人”按钮上设置有属性：android:onClick="selectContact"，所以，当点击“选择联系人”时会执行该方法
     * @param view
     */
    public void  selectContact(View view){
        Intent intent = new Intent(this,SlesctContactActivity.class);
        //激活一个带返回值的activity。参数二：请求码
        startActivityForResult(intent,0);
    }
    /**
     * 被激活的Activity将返回的结果数据存放在Intent中，这里的Intent和被激活的Activity返回
     * 数据时所使用的是同一个Intent。
     * 注意：如果希望数据能够正常返回，Activity的启动模式不能够设置为singletask模式
     */
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if (data!=null){
            String number = data.getStringExtra("number");
            et_setup3_number.setText(number);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
    //点击“下一步”执行的方法
    @SuppressLint("WrongConstant")
    public void next(View view){
        String number = et_setup3_number.getText().toString().trim();
        if(TextUtils.isEmpty(number)){
            Toast.makeText(this, "安全号码不能为空", 0).show();
            return;
        }
        //将EditText中的安全号码持久化起来，也方便数据的回显
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("safemuber", number);
        editor.commit();

        Intent intent = new Intent(this,Setup4Activity.class);
        startActivity(intent);
        finish();
        //自定义一个平移的动画效果。参数一：界面进入时的动画效果 ， 参数二：界面出去时的动画效果
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
    //点击“上一步”执行的方法
    public void pre(View view){
        Intent intent = new Intent(this,Setup2Activity.class);
        startActivity(intent);
        finish();
        //自定义一个透明度变化的动画效果。参数一：界面进入时的动画效果 ， 参数二：界面出去时的动画效果
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }
}
