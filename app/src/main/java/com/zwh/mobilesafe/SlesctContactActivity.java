package com.zwh.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zwh.mobilesafe.domain.ContactInfo;
import com.zwh.mobilesafe.engine.ContactInfoProvider;

import java.util.List;

/**
 * Created by Administrator on 2017/11/10 0010.
 */

class SlesctContactActivity extends Activity{
    private ListView lv_select_contact;
    private ContactInfoProvider provider;
    private List<ContactInfo> infos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contact);
        lv_select_contact = (ListView)findViewById(R.id.lv_select_contact);
        provider = new ContactInfoProvider(this);
        infos = provider.getContactInfos();
        lv_select_contact.setAdapter(new ContactAdapte());
        lv_select_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {
                //获取到点击item对应的联系人的信息对象
                ContactInfo info= (ContactInfo) lv_select_contact.getItemAtPosition(i);
                //获取到该联系人的号码
                String number = info.getPhone();
                //将该联系人的号码返回给激活当前Activity的Activity
                Intent data = new Intent();
                //将数据存入，用于返回给Activity
                data.putExtra("number",number);
                //返回数据，参数一：返回结果码  参数二：返回数据
                setResult(0,data);
                //关闭当前的activity
                finish();
            }
        });
    }

    /**
     * 展现所有联系人
     */
    private class ContactAdapte extends BaseAdapter  {
        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int i) {
            return infos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ContactInfo info = infos.get(i);
            TextView textView = new TextView(getApplicationContext());
            textView.setText(info.getName()+"\n"+info.getPhone());
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(24);
            return textView;
        }
    }
}
