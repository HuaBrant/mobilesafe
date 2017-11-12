package com.zwh.mobilesafe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Formatter;

import com.zwh.mobilesafe.domain.AppInfo;
import com.zwh.mobilesafe.engine.AppInfoProvider;
import com.zwh.mobilesafe.utils.DensityUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by zwh on 2017/11/11 0011.
 */

class AppManagerActivity extends Activity implements View.OnClickListener{
    protected static final int LOAD_APP_FINISH = 50;
    private static final String TAG ="AppManagerActivity";
    private TextView tv_appmanager_men_avail;//show mobile available memory
    private TextView tv_appmanager_sd_avail;//show Sdcard available memory
    private ListView lv_appmanager;//show usr app and system app
    private LinearLayout ll_loading;//ProgressBar's father widget for control its child widget to show
    private PackageManager pm;//equivalently to system appManager (to get all mobile app)
    private List<AppInfo> appInfos;//sava all app's info;
    private List<AppInfo> userappInfos;
    private List<AppInfo> systemappInfos;
    //PopupWindow's contenView corresponding three widget
    private LinearLayout ll_uninstall;
    private LinearLayout ll_start;
    private LinearLayout ll_share;
    private PopupWindow popupWindow;
    private String clickedpackname;
    //send message to main thread when child thread finish
    @SuppressLint("HandlerLeak")
    private Handler handler =new Handler(){
        public void handleMessage(Message message){
            switch(message.what){
                case LOAD_APP_FINISH:
                    ll_loading.setVisibility(View.INVISIBLE);
                    lv_appmanager.setAdapter(new AppManagerAdapter());
                    break;
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_manager);
        tv_appmanager_men_avail =findViewById(R.id.tv_appmanager_mem_avail);
        tv_appmanager_men_avail  =findViewById(R.id.tv_appmanager_sd_avail);
        ll_loading = findViewById(R.id.ll_appmanager_loading);
        lv_appmanager = findViewById(R.id.lv_appmanager);
        tv_appmanager_men_avail.setText("内存可用"+getAvailROMSize());
        tv_appmanager_sd_avail.setText("SD卡可用"+getAvailSDSize());

        pm=getPackageManager();
        //loading all app data
        fillData();
        //set onClick for listView
        lv_appmanager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //we should close existed PopupWindow when usr onClick Item
                dismissPopupWindow();
                View contentView = View.inflate(getApplicationContext(),
                        R.layout.popup_item,null);
                ll_uninstall = findViewById(R.id.ll_popup_uninstall);
                ll_start = findViewById(R.id.ll_popup_start);
                ll_share = findViewById(R.id.ll_popup_share);
                //set listener for uninstall start share
                ll_share.setOnClickListener(AppManagerActivity.this);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_start.setOnClickListener(AppManagerActivity.this);
                LinearLayout ll_popup_container = contentView
                        .findViewById(R.id.ll_popup_container);
                //set a ScaleAnimtaion
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        0.0f, 1.0f, 0.0f, 1.0f);
                //set play time
                scaleAnimation.setDuration(300);
                //get  current item object
                Object object = lv_appmanager.getItemAtPosition(i);
                //if item is system ,set a flag for PopupWindow's uninstall ,judge the flag ,system app bad uninstall
                if (object instanceof AppInfo){
                    AppInfo appInfo = (AppInfo) object;
                    clickedpackname = appInfo.getPackname();
                    if (appInfo.isUserapp()){
                        ll_uninstall.setTag(true);
                    }else {
                        ll_uninstall.setTag(false);
                    }
                }else {
                    return;
                }
                //get distance to the Top and bottom
                int top = view.getTop();
                int bottom = view.getBottom();
                //create a PopupWindow should set size or else can not show
                popupWindow = new PopupWindow(contentView, DensityUtil.dis2px(getApplicationContext(),
                        200), bottom-top+DensityUtil.dis2px(getApplicationContext(),20));
                //notice: must set backgroundDrawable or else will make problem with how do for animation and focal
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT
                ));
                //get location for the window if the item
                int [] location = new int[2];
                view.getLocationInWindow(location);
                popupWindow.showAtLocation(view, Gravity.TOP|Gravity.LEFT,
                        location[0]+20,location[1]);
                ll_popup_container.startAnimation(scaleAnimation);
            }
        });
        /**
         * close PopupWindow when usr slide window
         */
        lv_appmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                dismissPopupWindow();
            }
        });
    }

    /**
     * we should close existed PopupWindow when usr onClick Item
     */
    private void dismissPopupWindow() {
        if (popupWindow !=null && popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /**
     * loading all appData
     */
    private void fillData() {
        //ll_loading view show ProgressBar and textView show loading when loading data
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                AppInfoProvider provider = new AppInfoProvider(AppManagerActivity.this);
                appInfos =provider.getInstallenApps();
                initAppInfo();
                Message message =Message.obtain();
                message.what = LOAD_APP_FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }

    /**
     * Init appInfo list
     */
    private void initAppInfo() {
        systemappInfos = new ArrayList<AppInfo>();
        userappInfos = new ArrayList<AppInfo>();
        for (AppInfo appInfo :appInfos){
            if (appInfo.isUserapp()){
                userappInfos.add(appInfo);
            }else {
                systemappInfos.add(appInfo);
            }
        }
    }

    /**
     * clich event of PopupWindow
     * @param view
     */
    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_popup_share:
                Log.i(TAG, "分享");
                shareApplication();
                break;
            case R.id.ll_popup_start:
                Log.i(TAG, "开启");
                startApplication();
                break;
            case R.id.ll_popup_uninstall:
                boolean tag =(boolean) view.getTag();
                if (tag){
                    Log.i(TAG, "卸载"+clickedpackname);
                    uninstallApplication();
                }else {
                    Toast.makeText(this, "系统应用不能被卸载", 1).show();
                }
                Log.i(TAG, "onClick: ");
        }

    }

    /**
     * uninstall app
     */
    private void uninstallApplication() {
        dismissPopupWindow();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + clickedpackname));
        //uninstallApp sdCard changed should upData
        startActivityForResult(intent, 1);

    }

    @SuppressLint("WrongConstant")
    private void startApplication() {
        dismissPopupWindow();
        Intent intent = new Intent();
        PackageInfo packinfo;
        try {
            //PackageManager.GET_ACTIVITIES告诉包管理者，在解析清单文件时，只解析Activity对应的节点
            packinfo = pm.getPackageInfo(clickedpackname,
                    PackageManager.GET_ACTIVITIES);

            ActivityInfo[] activityinfos = packinfo.activities;
            //判断清单文件中是否存在Activity对应的节点
            if (activityinfos != null && activityinfos.length > 0) {
                //启动清单文件中的第一个Activity节点
                String className = activityinfos[0].name;
                intent.setClassName(clickedpackname, className);
                startActivity(intent);
            } else {
                Toast.makeText(this, "不能启动当前应用", 0).show();
            }
        } catch (PackageManager.NameNotFoundException e) {//使用C语言实现的应用程序，在DDMS中没有对应的包名
            e.printStackTrace();
            Toast.makeText(this, "不能启动当前应用", 0).show();
        }
    }

    /**
     * share app
     */
    private void shareApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.categroy.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra("subject","分享的标题");
        intent.putExtra("sms_body", "推荐你使用一款软件"+clickedpackname);
        intent.putExtra(Intent.EXTRA_TEXT, "extra_text");
        startActivity(intent);
    }

    /**
     * get Avail Rom Size
     * @return
     */
    public String getAvailROMSize() {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs =new StatFs(file.getPath());
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availROMSize = blockSize*availableBlocks;
        return Formatter.formatFileSize(this,availROMSize);
    }

    /**
     * get Avail SD size
     * @return
     */
    public String getAvailSDSize() {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long titalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();
        long blocksize = statFs.getBlockSize();
        long availSDSize = availableBlocks*blocksize;
        return Formatter.formatFileSize(this,availSDSize);
    }

    /**
     * App manager adapter
     */
    private class AppManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userappInfos.size()+systemappInfos.size()+2;
        }

        @Override
        public Object getItem(int i) {
            if(i==0){
                return i;
            }else if (i <=userappInfos.size()){
                int newi =i-1;
                return userappInfos.get(newi);
            }else if (i==(userappInfos.size()+1)){
                return i;
            }else {
                int newi =i-userappInfos.size()-2;
                return systemappInfos.get(newi);
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int i, View convertview, ViewGroup viewGroup) {
            if (i==0){
                TextView textView =new TextView(getApplicationContext());
                textView.setTextSize(20);
                textView.setText("用户程序 (" + userappInfos.size() + ")");
                return textView;
            }else if (i < userappInfos.size()){
                int newi = i-1;
                View view;
                ViewHolder holder;
                if (convertview ==null||convertview instanceof  TextView){
                    view =View.inflate(getApplicationContext(),
                            R.layout.app_manager_item,null);
                    holder = new ViewHolder();
                    holder.iv_icon = view.findViewById(R.id.iv_appmanger_icon);
                    holder.tv_name = view.findViewById(R.id.tv_appmanager_appname);
                    holder.tv_version = view.findViewById(R.id.tv_appmanager_appversion);
                    view.setTag(holder);
                }else {
                    view = convertview;
                    holder = (ViewHolder) view.getTag();
                }
                AppInfo appInfo = userappInfos.get(newi);
                holder.iv_icon.setImageDrawable(appInfo.getAppicon());
                holder.tv_version.setText("版本号: "+appInfo.getVersion());
                holder.tv_name.setText(appInfo.getAppname());
                return view;
            }else if (i == (userappInfos.size()+1)){
                TextView textView = new TextView(getApplicationContext());
                textView.setTextSize(20);
                textView.setText("系统程序 (" + systemappInfos.size() + ")");
                return textView;
            }else {
                int newi =i - userappInfos.size() -2;
                ViewHolder holder;
                View view;
                if (convertview ==null||convertview instanceof  TextView){
                    view =View.inflate(getApplicationContext(),
                            R.layout.app_manager_item,null);
                    holder = new ViewHolder();
                    holder.iv_icon = view.findViewById(R.id.iv_appmanger_icon);
                    holder.tv_name = view.findViewById(R.id.tv_appmanager_appname);
                    holder.tv_version = view.findViewById(R.id.tv_appmanager_appversion);
                    view.setTag(holder);
                }else {
                    view = convertview;
                    holder = (ViewHolder) view.getTag();
                }
                AppInfo appInfo = systemappInfos.get(newi);
                holder.iv_icon.setImageDrawable(appInfo.getAppicon());
                holder.tv_version.setText("版本号: "+appInfo.getVersion());
                holder.tv_name.setText(appInfo.getAppname());
                return view;
            }
        }

        /**
         * shield two textView Cliched
         * @param i
         * @return
         */
        public boolean isEnabled (int i){
            if(i==0||i==(userappInfos.size()+1)){
                return false;
            }
            return super.isEnabled(i);
        }
    }

    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_version;
    }
    public Intent getIntent(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        @SuppressLint("WrongConstant")
        List<ResolveInfo> resoveInfo = pm.queryIntentActivities(intent,
                PackageManager.GET_INTENT_FILTERS
                        | PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo info : resoveInfo) {
            // info.activityInfo.packageName;
        }
        return null;
    }
    @Override
    protected void onDestroy() {
        dismissPopupWindow();
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            // 通知界面更新数据.
            fillData();
            tv_appmanager_sd_avail.setText("SD卡可用" + getAvailSDSize());
            tv_appmanager_men_avail.setText("内存可用:" + getAvailROMSize());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
