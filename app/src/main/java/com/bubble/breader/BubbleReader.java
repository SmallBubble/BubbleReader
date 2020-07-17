package com.bubble.breader;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

/**
 * @author Bubble
 * @date 2020/7/17
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 初始化阅读器
 */
public class BubbleReader {
    private static BubbleReader sReader;
    private Context mContext;

    public static synchronized BubbleReader getInstance() {
        if (sReader == null) {
            sReader = new BubbleReader();
        }
        return sReader;
    }

    public void init(Context context) {
        mContext = context;
    }

    public boolean isInit() {
        return mContext == null;
    }

    public void getColor(@ColorRes int colorId) {
        ContextCompat.getColor(mContext, colorId);
    }
}
