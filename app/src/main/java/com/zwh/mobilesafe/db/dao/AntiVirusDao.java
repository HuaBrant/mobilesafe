package com.zwh.mobilesafe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zwh on 2017/11/14 0014.
 */

public class AntiVirusDao {
    Context context;

    public AntiVirusDao(Context context) {
        this.context = context;
    }
    public String getVirusInfo(String md5){
        //默认情况下，没有获取到病毒信息
        String result = null;
        //病毒数据库的路径
        String path = "/data/data/com.zwh.mobilesafe/files/antivirus.db";
        //打开数据库
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
            SQLiteDatabase.OPEN_READONLY);
		if(db.isOpen()){
        //执行查询操作，返回一个结果集
        Cursor cursor = db.rawQuery("select desc from datable where md5=?", new String[]{md5});
        if(cursor.moveToFirst()){
            result = cursor.getString(0);
        }
        //必须关闭系统的游标，如果没有关闭，即使关闭了数据库，也容易报出内存泄漏的异常信息
        cursor.close();
        db.close();
       }
		return result;
    }
}
