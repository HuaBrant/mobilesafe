package com.zwh.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/11 0011.
 */

class TaskManagerActivity extends Activity implements View.OnClickListener {
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
        List<ProcessInfo> allRuningProcessIndos = provider.getProcessInfos();
        for (ProcessInfo info : allRuningProcessIndos) {
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

            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private void initView() {
        btUser = (Button) findViewById(R.id.bt_user);
        btSystem = (Button) findViewById(R.id.bt_system);
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

            }
            return null;
        }

        private  class ViewHolder {
            ImageView iv_con;
            TextView tv_name;
            TextView tv_Mem;
            CheckBox cb_manager;

            ViewHolder(View view) {
                this.iv_con = (ImageView) view.findViewById(R.id.iv_taskmanger_icon);
                this.tv_name = (TextView) view.findViewById(R.id.tv_taskmanager_appname);
                this.tv_Mem = (TextView) view.findViewById(R.id.tv_taskmanager_mem);
                this.cb_manager = (CheckBox) view.findViewById(R.id.cb_taskmanager);
            }
        }
    }

    private class SystemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }

}
