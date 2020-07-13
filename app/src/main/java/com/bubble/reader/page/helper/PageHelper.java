package com.bubble.reader.page.helper;

import android.graphics.Canvas;

import com.bubble.reader.bean.PageBean;

/**
 * @author Bubble
 * @date 2020/7/11
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public abstract class PageHelper {
    abstract void onDraw(Canvas canvas, PageBean pageBean);
}
