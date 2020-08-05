package com.bubble.breader.widget.draw.base;

import android.graphics.Canvas;

import com.bubble.breader.widget.PageSettings;
import com.bubble.breader.widget.PageView;

/**
 * @author Bubble
 * @date 2020/7/14
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc TODO
 */
public abstract class DrawHelper implements IDrawHelper {
    protected PageView mPageView;
    /**
     * 页面宽度
     */
    protected int mPageWidth;
    /**
     * 页面高度
     */
    protected int mPageHeight;
    /**
     * 页面设置
     */
    protected PageSettings mSettings;

    private boolean mInitialized;
    /*=======================================初始化=========================================*/

    public DrawHelper(PageView pageView) {
        mPageView = pageView;
    }

    @Override
    public void init() {
        mInitialized = true;
        mSettings = mPageView.getSettings();
        mPageWidth = mPageView.getMeasuredWidth();
        mPageHeight = mPageView.getMeasuredHeight();
        if (!mSettings.isShowBottom()) {
            // 不显示底部 需要减掉底部高度
            mPageHeight = mPageHeight - mSettings.getBottomHeight();
        }
        if (!mSettings.isShowTop()) {
            // 不显示顶部 减掉顶部高度
            mPageHeight = mPageHeight - mSettings.getTopHeight();
        }
    }

    @Override
    public void draw(Canvas canvas) {

    }

    public boolean isInit() {
        return mInitialized;
    }
}
