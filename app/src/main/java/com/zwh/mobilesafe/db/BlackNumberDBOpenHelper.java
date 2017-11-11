package com.zwh.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/11/11 0011.
 */

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper{
    public BlackNumberDBOpenHelper(Context context){
        //参数一：上下文对象，参数二：数据库名称，参数三：游标工厂对象，null表示使用系统默认的，参数四：当前数据库的版本号
        super(context,"blacknumber",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //黑名单号码的表结构 （_id , 黑名单号码, 拦截模式（0 表示电话拦截 ，1表示短信拦截 ，2表示全部拦截（电话&短信））
        sqLiteDatabase.execSQL("create table blacknumber " +
                "(_id integer primary key autoincrement, number varchar(20), mode integer)");
    }
    /**
     * 当数据库的版本号升级的时候 调用的方法.
     * 一般用于升级程序后,更新数据库的表结构.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
