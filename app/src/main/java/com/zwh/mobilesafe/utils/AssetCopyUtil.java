package com.zwh.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zwh on 2017/11/14 0014.
 */

public class AssetCopyUtil {
    Context context;

    public AssetCopyUtil(Context context) {
        this.context = context;
    }
    public boolean copyFile(String srefilname , File file,ProgressDialog progressDialog){
        AssetManager assetManager=context.getAssets();
        try {
            InputStream is=assetManager.open(srefilname);
            progressDialog.setMax(is.available());
            FileOutputStream fos = new FileOutputStream(file);
            byte [] buffer = new byte[1024];
            int len=0;
            int press =0;
            while ((len=is.read(buffer))!=-1){
                fos.write(buffer,0,len);
                press+=len;
                progressDialog.setProgress(press);
            }
            fos.flush();
            fos.close();
            is.close();
            return true
                    ;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static File copy1(Context context, String filename,
                             String destfilename, ProgressDialog pd) {

        try {
            InputStream in = context.getAssets().open(filename);
            int max = in.available();
            if (pd != null) {
                pd.setMax(max);
            }

            File file = new File(destfilename);
            OutputStream out = new FileOutputStream(file);
            byte[] byt = new byte[1024];
            int len = 0;
            int total = 0;
            while ((len = in.read(byt)) != -1) {
                out.write(byt, 0, len);
                total += len;
                if (pd != null) {
                    pd.setProgress(total);
                }
            }
            out.flush();
            out.close();
            in.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
