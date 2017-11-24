package com.zwh.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zwh.mobilesafe.domain.ContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10 0010.
 */

public class ContactInfoProvider {
    private Context context;
    public ContactInfoProvider(Context context){
        this.context=context;
    }

    /**
     * 返回所有的联系人的信息
     * @return
     */
    public List<ContactInfo> getContactInfos(){
        List<ContactInfo> infos = new ArrayList<ContactInfo>();//将所有联系人存入该集合
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri datauri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{"contact_id"},null,null,null);
        while (cursor.moveToNext()) {//移动游标
            //因为我们只需要查询一列数据-联系人的id，所以我们传入0
            String id = cursor.getString(0);
            //用于封装每个联系人的具体信息
            ContactInfo info= new ContactInfo();
            //得到id后，我们通过该id来查询data表中的联系人的具体数据（data表中的data1中的数据）。参数二：null，会将所有的列返回回来
            //参数三：选择条件    返回一个在data表中查询后的结果集
            Cursor dataCursor = context.getContentResolver().query(datauri,
                    null,"raw_contact_id=?",new String[]{id},null);
            while (dataCursor.moveToNext()){
                //dataCursor.getString(dataCursor.getColumnIndex("mimetype"))获取data1列中具体数据的数据类型，这里判断的是联系人的姓名
                if ("vnd.android.cursor.item/name".equals(dataCursor
                .getString(dataCursor.getColumnIndex("mimtype")))){
                    info.setName(dataCursor.getString(dataCursor
                    .getColumnIndex("data1")));
                }else if ("vnd.android.cursor.iten/phone_v2".equals(dataCursor
                .getString(dataCursor.getColumnIndex("mimetype")))){
                    info.setPhone(dataCursor.getString(dataCursor
                    .getColumnIndex("data1")));
                }

            }
            infos.add(info);
            info = null;
            dataCursor.close();

        }
        cursor.close();
        return infos;
    }
}
