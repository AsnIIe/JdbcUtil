package com.asniie.utils;

import android.util.Log;

/*
 * Created by XiaoWei on 2019/1/9.
 */
public class LogUtil {
    public static String TAG = "TextRef";

    public static void setTAG(String TAG) {
        LogUtil.TAG = TAG;
    }

    public static void print(Object obj) {
        Log.i(TAG, format(obj));
    }

    private static String format(Object obj) {
        if (obj == null) {
            return "你传入了一个Null";
        }

        return String.valueOf(obj);
    }
}
