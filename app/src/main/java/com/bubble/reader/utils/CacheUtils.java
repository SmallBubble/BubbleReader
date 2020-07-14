package com.bubble.reader.utils;

import com.tencent.mmkv.MMKV;

/**
 * @author Bubble
 * @date 2020/7/9
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 缓存工具类
 */
public class CacheUtils {
    private static final String CACHE_NAME = "louLanCache";
    private static MMKV sKv = MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, CACHE_NAME);

    public static void putString(String key, String value) {
        sKv.putString(key, value);
    }

    public static void putInteger(String key, Integer value) {
        sKv.putInt(key, value);
    }

    public static void putBoolean(String key, Boolean value) {
        sKv.putBoolean(key, value);
    }

    public static void putFloat(String key, Float value) {
        sKv.putFloat(key, value);
    }

    public static void putLong(String key, Long value) {
        sKv.putLong(key, value);
    }

    public static String getString(String key, String defValue) {
        return sKv.getString(key, defValue);
    }

    public static String getString(String key) {
        return sKv.getString(key, "");
    }

    public static Boolean getBoolean(String key, Boolean defValue) {
        return sKv.getBoolean(key, defValue);
    }

    public static Boolean getBoolean(String key) {
        return sKv.getBoolean(key, false);
    }

    public static Integer getInt(String key, Integer defValue) {
        return sKv.getInt(key, defValue);
    }

    public static Integer getInt(String key) {
        return sKv.getInt(key, 0);
    }

    public static Float getFloat(String key, Float defValue) {
        return sKv.getFloat(key, defValue);
    }

    public static Float getFloat(String key) {
        return sKv.getFloat(key, 0);
    }

    public static Long getLong(String key, Long defValue) {
        return sKv.getLong(key, defValue);
    }

    public static Long getLong(String key) {
        return sKv.getLong(key, 0);
    }

    public static void clearAll() {
        sKv.clearAll();
    }
}
