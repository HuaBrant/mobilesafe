package com.zwh.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zwh.mobilesafe.domain.ProcessInfo;
import com.zwh.mobilesafe.engine.ProcessInfoProvider;
import com.zwh.mobilesafe.view.MyToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/11 0011.
 */

public class TaskManagerActivity extends Activity implements View.OnClickListener {
    private boolean showUserApp;
    private UserAdapter userAdapter;
    private SystemAdapter systemAdapter;
    //get phone process
    private ProcessInfoProvider provider;
    //set an textView for system ,the textView show :kill system app will makes system instability
    private TextView tvheader;
    private List<ProcessInfo> userProcessInfos;
    private List<ProcessInfo> systemProcessInfos;
    //switchover usrApp and systemApp (response Button selectAll and one side had onClick:judge userApp or systemApp
    private Button btUser;
    private Button btSystem;
    private Button btSelectAll;
    private Button btKeyClear;
    private ListView lvUsertask;
    private ListView lvSystemtask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_manager);
        initView();
        showUserApp = true;
        provider = new ProcessInfoProvider(this);
        userProcessInfos = new ArrayList<ProcessInfo>();
        systemProcessInfos = new ArrayList<ProcessInfo>();
        List<ProcessInfo> allRunningProcessInfos = provider.getProcessInfos();
        for (ProcessInfo info : allRunningProcessInfos) {
            if (info.isUserprocess()) {
                userProcessInfos.add(info);
            } else {
                systemProcessInfos.add(info);
            }
        }
        lvUsertask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_taskmanager);
                ProcessInfo info = (ProcessInfo) lvUsertask.getItemAtPosition(i);
                if (info.getPackname().equals(getPackageName())){
                    return;
                }
                if (info.isChecked()){
                    info.setChecked(false);
                    checkBox.setChecked(true);
                }else {
                    info.setChecked(true);
                    checkBox.setChecked(true);
                }
            }
        });
        lvSystemtask.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {
                CheckBox checkBox = (CheckBox) view.findViewById(R
                        .id.cb_taskmanager);
                ProcessInfo info = (ProcessInfo) lvSystemtask
                        .getItemAtPosition(i);
                if (info.isChecked()){
                    info.setChecked(false);
                    checkBox.setChecked(true);
                }else {
                    info.setChecked(true);
                    checkBox.setChecked(true);
                }
            }
        });
        btUser.setOnClickListener(this);
        btUser.setBackground(getResources().getDrawable(R.
                drawable.bt_pressed));
        btSystem.setOnClickListener(this);
        btSystem.setBackground(getResources().getDrawable(R.
                drawable.bt_normal));
        btSelectAll.setOnClickListener(this);
        btKeyClear.setOnClickListener(this);
        //default entry userView
        lvSystemtask.setVisibility(View.GONE);
        userAdapter = new UserAdapter();
        lvUsertask.setAdapter(userAdapter);
        tvheader = new TextView(getApplicationContext());
        tvheader.setText("杀死系统进程会导致系统不稳定");
        tvheader.setBackgroundColor(Color.YELLOW);
        lvSystemtask.addHeaderView(tvheader);
        systemAdapter = new SystemAdapter();
        lvSystemtask.setAdapter(systemAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_user:
                if (tvheader !=null){
                    lvSystemtask.removeHeaderView(view);
                    tvheader =null;
                }
                showUserApp =true;
                btUser.setBackground(getResources().getDrawable(R.
                        drawable.bt_pressed));
                btSystem.setBackground(getResources().getDrawable(R.
                        drawable.bt_normal));
                lvSystemtask.setVisibility(View.INVISIBLE);
                lvUsertask.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_system:
                showUserApp =false;
                btUser.setBackground(getResources().getDrawable(R.
                        drawable.bt_normal));
                btSystem.setBackground(getResources().getDrawable(R.
                        drawable.bt_pressed));
                lvSystemtask.setVisibility(View.VISIBLE);
                lvUsertask.setVisibility(View.INVISIBLE);
                break;
            case R.id.bt_select_all_task:
                selectAll(view);
                break;
            case R.id.bt_key_clear_task:
                oneKeyClear(view);
                break;
        }

    }

    public void selectAll(View view) {
        if (showUserApp){
            for (ProcessInfo info : userProcessInfos){
                info.setChecked(true);
                userAdapter.notifyDataSetChanged();
            }
        }else {
            for (ProcessInfo info: systemProcessInfos){
                info.setChecked(true);
                userAdapter.notifyDataSetChanged();
            }
        }
    }
    public void oneKeyClear(View view){
        ActivityManager activityManager = (ActivityManager)
                getSystemService(ACTIVITY_SERVICE);
        int count=0;
        long memsize =0;
        List<ProcessInfo> killedProcessInfos = new ArrayList<ProcessInfo>();
        if (showUserApp){
            for (ProcessInfo info : userProcessInfos){
                if (info.isChecked()){
                    count++;
                    memsize+=info.getMemsize();
                    assert activityManager != null;
                    activityManager.killBackgroundProcesses(info.getPackname());
                    killedProcessInfos.add(info);
                }
            }
        }else {
            for (ProcessInfo info : systemProcessInfos) {
                if (info.isChecked()) {
                    count++;
                    memsize += info.getMemsize();
                    assert activityManager != null;
                    activityManager.killBackgroundProcesses(info.getPackname());
                    killedProcessInfos.add(info);

                }
            }
        }
        if (showUserApp){
            userAdapter.notifyDataSetChanged();
        }else {
            systemAdapter.notifyDataSetChanged();
        }
        MyToast.showToast(
                this,
                "杀死了" + count + "个进程,释放了"
                        + Formatter.formatFileSize(this, memsize) + "内存");
    }

    private void initView() {
        btUser = (Button) findViewById(R.id.bt_user);
        btSystem = (Button) findViewById(R.id.bt_system);
        btKeyClear= (Button) findViewById(R.id.bt_key_clear_task);
        btSelectAll = (Button) findViewById(R.id.bt_select_all_task);
        lvUsertask = (ListView) findViewById(R.id.lv_usertask);
        lvSystemtask = (ListView) findViewById(R.id.lv_systemtask);
    }

    private class UserAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userProcessInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return userProcessInfos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.task_manager_item, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }else {
                view =convertView;
                holder =(ViewHolder)view.getTag();
            }
            ProcessInfo info = userProcessInfos.get(i);
            if (info.getPackname().equals(getPackageName())){
                holder.cb_manager.setVisibility(View.INVISIBLE);
            }else {
                holder.cb_manager.setVisibility(View.VISIBLE);
            }
            holder.tv_Mem.setText(Formatter.formatFileSize(getApplicationContext(),
                    info.getMemsize()));
            holder.iv_icon.setImageDrawable(info.getIcon());
            holder.tv_name.setText(info.getAppname());
            holder.cb_manager.setChecked(info.isChecked());
            return view;
        }

    }
    private  static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_Mem;
        CheckBox cb_manager;

        ViewHolder(View view) {
            iv_icon = (ImageView) view.findViewById(R.id.iv_taskmanger_icon);
            this.tv_name = (TextView) view.findViewById(R.id.tv_taskmanager_appname);
            this.tv_Mem = (TextView) view.findViewById(R.id.tv_taskmanager_mem);
            this.cb_manager = (CheckBox) view.findViewById(R.id.cb_taskmanager);
        }
    }

    private class SystemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return systemProcessInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return systemProcessInfos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.task_manager_item, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }else {
                view =convertView;
                holder =(ViewHolder)view.getTag();
            }
            ProcessInfo info = systemProcessInfos.get(i);
            if (info.getPackname().equals(getPackageName())){
                holder.cb_manager.setVisibility(View.INVISIBLE);
            }else {
                holder.cb_manager.setVisibility(View.VISIBLE);
            }
            holder.tv_Mem.setText(Formatter.formatFileSize(getApplicationContext(),
                    info.getMemsize()));
            holder.iv_icon.setImageDrawable(info.getIcon());
            holder.tv_name.setText(info.getAppname());
            holder.cb_manager.setChecked(info.isChecked());
            return view;
        }
    }

}
