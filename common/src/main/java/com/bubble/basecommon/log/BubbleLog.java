package com.bubble.basecommon.log;

import android.util.Log;

import com.bubble.basecommon.BuildConfig;

/**
 * @author Bubble
 * @date 2020/6/20
 * @email 1337986595@qq.com
 * @Desc
 */
public class BubbleLog {
    private final static String TAG = BubbleLog.class.getSimpleName();

    public static void e(Object message) {
        Log.e(TAG, String.valueOf(message));
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
    }

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void d(String tag, String message) {
        Log.d(tag, message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
    }

    public static void w(String tag, String message) {
        Log.w(tag, message);
    }

    public static void v(String message) {
        Log.v(TAG, message);
    }

    public static void v(String tag, String message) {
        Log.v(tag, message);
    }
}
