package com.bubble.reader.widget.draw.base;

import android.graphics.Canvas;

import com.bubble.reader.widget.PageSettings;
import com.bubble.reader.widget.PageView;

public class DrawHelper implements IDrawHelper {
    protected PageView mPageView;
    /**
     * 页面宽度
     */
    protected int mPageWidth;
    /**
     * 页面高度
     */
    protected int mPageHeight;
    protected PageSettings mSettings;

    /*=======================================初始化=========================================*/
    public DrawHelper(PageView pageView) {
        mPageView = pageView;
    }

    @Override
    public void init() {
        mSettings = mPageView.getSettings();
        mPageWidth = mPageView.getMeasuredWidth();
        mPageHeight = mPageView.getMeasuredHeight();
    }

    @Override
    public void draw(Canvas canvas) {

    }
}
