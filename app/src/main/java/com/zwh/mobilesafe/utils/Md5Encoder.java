package com.zwh.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/11/10 0010.
 */

public class Md5Encoder {
    public static String encoder(String password){
        try {
            //获取到数字消息的摘要器
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //执行加密操作
            byte [] result =digest.digest(password.getBytes());
            StringBuffer sb=new StringBuffer();
            //将每个byte字节的数据转换成16进制的数据
            for (int i=0;i<result.length;i++){
                int number = result[i]&0xff;//加密
                String str =Integer.toHexString(number);//将十进制的number转换成十六进制数据
                if (str.length()==1){//判断加密后的字符的长度，如果长度为1，则在该字符前面补0
                    sb.append("0");
                    sb.append(str);
                }else {
                    sb.append(str);
                }
            }
            return sb.toString();//将加密后的字符转成字符串返回
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
