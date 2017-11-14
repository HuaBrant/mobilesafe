package com.zwh.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zwh.mobilesafe.db.dao.NumberAddressDao;

/**
 * Created by zwh on 2017/11/14 0014.
 */

class NumberQueryActivity extends Activity {
    /**
     * 请输入查询的电话号码
     */
    private EditText etNumberQuery;
    /**
     * 归属地:
     */
    private TextView tvNumberAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_query);
        initView();
    }
    public void query(View view){
        String number=etNumberQuery.getText().toString().trim();
        // 判断要查询的号码是否为空
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "号码不能为空", Toast.LENGTH_LONG).show();
            // 使用动画工具来加载一个动画资源一个动画资源
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            // 当号码输入框中没有输入号码而点击“查询”时播放一个动画，用来提示用户输入号码后才可以执行查询操作。
            etNumberQuery.startAnimation(shake);
            return;
        } else {// 号码不为空时要返回归属地信息
            // 返回查询到的归属地信息
            String address = NumberAddressDao.getAddress(number);
            // 将归属地信息显示在屏幕上
            tvNumberAddress.setText(address);
        }
    }

    private void initView() {
        etNumberQuery = (EditText) findViewById(R.id.et_number_query);
        tvNumberAddress = (TextView) findViewById(R.id.tv_number_address);
    }
}
