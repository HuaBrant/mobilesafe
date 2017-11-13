package com.zwh.mobilesafe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zwh.mobilesafe.domain.TrafficInfo;
import com.zwh.mobilesafe.engine.TrafficInfoProvider;

import java.util.List;

/**
 * Created by Administrator on 2017/11/11 0011.
 */

public class TrafficInfoActivity extends Activity {
    private ImageView handle;
    private LinearLayout llLoading;
    private ListView lvTrafficManager;
    private FrameLayout content;
    private List<TrafficInfo> trafficInfos;
    private TrafficInfoProvider provider;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            llLoading.setVisibility(View.INVISIBLE);
            lvTrafficManager.setAdapter(new TrafficAdapter());
        }

        ;
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taffic_info);
        initView();
        new Thread() {
            @Override
            public void run() {
                trafficInfos = provider.getTrafficInfos();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initView() {
        handle = (ImageView) findViewById(R.id.handle);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        lvTrafficManager = (ListView) findViewById(R.id.lv_traffic_manager);
        content = (FrameLayout) findViewById(R.id.content);
    }

    private class TrafficAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return trafficInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return trafficInfos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view;
            ViewHolder holder;
            TrafficInfo info = trafficInfos.get(i);
            if (convertView == null) {
                view = View.inflate(getApplicationContext(),
                        R.layout.traffic_item,null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }else {
                view =convertView;
                holder =(ViewHolder) view.getTag();
            }
            holder.ivTrafficIcon.setImageDrawable(info.getIcon());
            holder.tvTrafficName.setText(info.getAppname());
            long rx =info.getDowndata();
            long tx =info.getUpdata();
            if(rx<0){
                rx=0;
            }
            if (tx<0){
                tx=0;
            }
            holder.tvTrafficRx.setText(Formatter.formatFileSize(getApplicationContext(),rx));
            holder.tvTrafficTx.setText(Formatter.formatFileSize(getApplicationContext(),tx));
            long total = rx+tx;
            holder.tvTrafficTotal.setText(Formatter.formatFileSize(getApplicationContext(),total));
            return view;
        }



    }

    static class ViewHolder {
        ImageView ivTrafficIcon;
        TextView tvTrafficName;
        TextView tvTrafficTx;
        TextView tvTrafficRx;
        TextView tvTrafficTotal;

        ViewHolder(View view) {
            this.ivTrafficIcon = (ImageView) view.findViewById(R.id.iv_traffic_icon);
            this.tvTrafficName = (TextView) view.findViewById(R.id.tv_traffic_name);
            this.tvTrafficTx = (TextView) view.findViewById(R.id.tv_traffic_tx);
            this.tvTrafficRx = (TextView) view.findViewById(R.id.tv_traffic_rx);
            this.tvTrafficTotal = (TextView) view.findViewById(R.id.tv_traffic_total);
        }
    }
}
