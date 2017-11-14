package com.zwh.mobilesafe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zwh.mobilesafe.utils.AssetCopyUtil;

import java.io.File;

/**
 * Created by Administrator on 2017/11/11 0011.
 */

public class AtoolsActivity extends Activity implements View.OnClickListener {
    protected static final int COPY_SUCCESS = 30;
    protected static final int COPY_FAILED = 31;
    protected static final int COPY_COMMON_NUMBER_SUCCESS = 32;
    private ProgressDialog pd;
    /**
     * 号码归属地查询
     */
    private TextView tvAtoolsAddressQuery;
    /**
     * 常用号码 查询
     */
    private TextView tvAtoolsCommonNum;
    /**
     * 程 序 锁
     */
    private TextView tvAtoolsApplock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atools);
        initView();
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

    }
    // 拷贝数据库是一个相对耗时的操作，拷贝完成后，给主线程发送消息
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 无论拷贝是否成功，都需要关闭进度显示条
            pd.dismiss();
            switch (msg.what) {
                case COPY_SUCCESS:
                    // 拷贝数据库成功后，进入号码归属地查询的界面
                    loadQueryUI();
                    break;
                case COPY_COMMON_NUMBER_SUCCESS:
                    //拷贝数据库成功后，进入常用号码显示的界面
                    loadCommNumUI();
                    break;
                case COPY_FAILED:
                    Toast.makeText(getApplicationContext(), "拷贝数据失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };

    private void loadCommNumUI() {
        Intent intent = new Intent(this, NumberQueryActivity.class);
        startActivity(intent);
    }

    private void loadQueryUI() {
        Intent intent = new Intent(this, CommonNumActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_atools_address_query:// 号码归属地查询
                // 创建出数据库要拷贝到的系统文件：data\data\包名\files\address.db
                final File file = new File(getFilesDir(), "address.db");
                // 判断数据库是否存在，如果存在，则直接进入号码归属地的查询界面，否则，执行拷贝动作
                if (file.exists() && file.length() > 0) {
                    // 数据库文件拷贝成功，进入查询号码归属地界面
                    loadQueryUI();
                } else {
                    // 数据库的拷贝.开始拷贝时需要开始显示进度条
                    pd.show();
                    // 拷贝数据库也是一个相对耗时的操作，在子线程中执行该操作
                    new Thread() {
                        public void run() {
                            AssetCopyUtil asu = new AssetCopyUtil(
                                    getApplicationContext());
                            // 返回拷贝成功与否的结果
                            boolean result = asu.copyFile("naddress.db", file, pd);
                            if (result) {// 拷贝成功
                                Message msg = Message.obtain();
                                msg.what = COPY_SUCCESS;
                                handler.sendMessage(msg);
                            } else {// 拷贝失败
                                Message msg = Message.obtain();
                                msg.what = COPY_FAILED;
                                handler.sendMessage(msg);
                            }
                        };
                    }.start();
                }
                break;
            case R.id.tv_atools_common_num:// 公用号码查询
                // 判读数据库是否已经拷贝到系统目录（ data/data/包名/files/address.db）
                final File commonnumberfile = new File(getFilesDir(),
                        "commonnum.db");
                if (commonnumberfile.exists() && commonnumberfile.length() > 0) {
                    loadCommNumUI();// 进入公共号码的显示界面
                } else {
                    // 数据库的拷贝.
                    pd.show();
                    // 拷贝数据库是一个相对耗时的工作，我们为其开启一个子线程
                    new Thread() {
                        public void run() {
                            // 将数据库拷贝到手机系统中
                            AssetCopyUtil asu = new AssetCopyUtil(
                                    getApplicationContext());
                            boolean result = asu.copyFile("commonnum.db",
                                    commonnumberfile, pd);
                            if (result) {// 拷贝成功
                                Message msg = Message.obtain();
                                msg.what = COPY_COMMON_NUMBER_SUCCESS;
                                handler.sendMessage(msg);
                            } else {// 拷贝失败
                                Message msg = Message.obtain();
                                msg.what = COPY_FAILED;
                                handler.sendMessage(msg);
                            }
                        };
                    }.start();
                }
                break;
            case R.id.tv_atools_applock://程序锁
                Intent applockIntent = new Intent(this,AppLockActivity.class);
                startActivity(applockIntent);
                break;
        }

    }

    private void initView() {
        tvAtoolsAddressQuery = (TextView) findViewById(R.id.tv_atools_address_query);
        tvAtoolsAddressQuery.setOnClickListener(this);
        tvAtoolsCommonNum = (TextView) findViewById(R.id.tv_atools_common_num);
        tvAtoolsCommonNum.setOnClickListener(this);
        tvAtoolsApplock = (TextView) findViewById(R.id.tv_atools_applock);
        tvAtoolsApplock.setOnClickListener(this);
    }
}
