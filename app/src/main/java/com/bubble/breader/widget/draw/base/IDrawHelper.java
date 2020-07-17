package com.bubble.breader.widget.draw.base;

import android.graphics.Canvas;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 绘制类需要实现的接口
 */
public interface IDrawHelper {
    /**
     * 初始化
     */
    void init();

    /**
     * 绘制内容
     *
     * @param canvas
     */
    void draw(Canvas canvas);

    /**
     * 回收资源
     */
    void recycle();
}
