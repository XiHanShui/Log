package com.socct.mylibrary.util;

import android.util.Log;

import com.socct.mylibrary.LogTracker;

/**
 * 是否打印日志
 *
 * @author WJ
 * @date 19-4-19
 */
public class LogUtils {


    private static final String LOG_TAG = "logLibrary";

    private static boolean isEnableLog;

    public static void init(boolean isEnable) {
        isEnableLog = isEnable;
    }


    public static void w(String message) {
        if (isEnableLog) {
            Log.w(LOG_TAG, message);
        }
    }


    public static void e(String message) {
        if (isEnableLog) {
            Log.e(LOG_TAG, message);
        }
    }


    public static void d(String message) {
        if (isEnableLog) {
            Log.e(LOG_TAG, message);
        }
    }


}
