package com.bubble.reader.utils;

/**
 * @author Bubble
 * @date 2020/7/13
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class CacheHelper implements ICacheHelper {

    private static final String FONT_SIZE = "fontSize";

    @Override
    public <T> T getConfig(String key, Object defValue) {

        if (defValue instanceof String) {
            return (T) CacheUtils.getString(key, (String) defValue);
        }


        return null;
    }

    @Override
    public void saveConfig(String key, Object value) {

    }

    public void setFontSize(int fontSize) {
        CacheUtils.putInteger(FONT_SIZE, fontSize);
    }

    public int getFontSize(String key) {
        return CacheUtils.getInt(FONT_SIZE, 12);
    }


}
