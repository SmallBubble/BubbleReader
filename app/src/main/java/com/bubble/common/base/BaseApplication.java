package com.bubble.common.base;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

/**
 * @author Bubble
 * @date 2020/6/29
 * @email 1337986595@qq.com
 * @Desc
 */
public class BaseApplication extends MultiDexApplication {
    private static Application sInstance;
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = sInstance = this;
    }

    public static Application getInstance() {
        return sInstance;
    }

    public static Context getContext() {
        return sContext;
    }
}
