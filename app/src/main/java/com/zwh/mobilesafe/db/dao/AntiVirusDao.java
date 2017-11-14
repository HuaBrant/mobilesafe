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
        String result =null;
        String path ="/data/data/com.zwh.mobilesafe/files/antivirus.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path,null,
                SQLiteDatabase.OPEN_READONLY);
        if (database.isOpen()){
            Cursor cursor=database.rawQuery("select desc from datable where md5=?",new String[]{md5});
            if (cursor.moveToFirst()){
                result=cursor.getString(0);
            }
            cursor.close();
            database.close();
        }
        return result;
    }
}
