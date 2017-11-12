package com.zwh.mobilesafe.utils;

import android.content.Context;

/**
 * Created by zwh on 2017/11/12 0012.
 */

public class DensityUtil {
    /**
     * make dp exchange px base on mobile resolution
     * @param context
     * @param dpValue
     * @return
     */
    public static int dis2px(Context context,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
    /**
     * make px exchange dp base on mobile resolution
     * @param context
     * @param dxValue
     * @return
     */
    public static int dis2dip(Context context,float dxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dxValue/scale+0.5f);
    }
}
