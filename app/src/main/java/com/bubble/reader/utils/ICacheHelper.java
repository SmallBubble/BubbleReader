package com.bubble.reader.utils;

/**
 * @author Bubble
 * @date 2020/7/13
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public interface ICacheHelper {
    <T> T getConfig(String key, Object defValue);

    void saveConfig(String key, Object value);
}
