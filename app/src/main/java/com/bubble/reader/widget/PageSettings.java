package com.bubble.reader.widget;

import android.graphics.Color;

import com.bubble.common.utils.Dp2PxUtil;

/**
 * @author Bubble
 * @date 2020/7/13
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class PageSettings {
    /**
     * 正文字体大小
     */
    private int mFontSize = 48;
    /**
     * 文字颜色
     */
    private int mFontColor = Color.parseColor("#ff0000");
    /**
     * 行间距
     */
    private int mLineSpace = 10;
    /**
     * 标题字体大小
     */
    private int mTitleFontSize = 18;
    /**
     * 段间距
     */
    private int mParagraphSpace = 12;

    /**
     * 内间距
     */
    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;
    private int mPaddingTop = 0;
    private int mPaddingBottom = 0;
    /**
     * 底部区域
     */
    private int mBottomHeight = 32;
    /**
     * 顶部字体大小
     */
    private int mBottomFontSize = Dp2PxUtil.dip2px(12);
    /**
     * 顶部tips字体颜色
     */
    private int mBottomFontColor = 0x333333;

    /**
     * 顶部区域
     */
    private int mTopHeight = 32;

    /**
     * 顶部字体大小
     */
    private int mTopFontSize = Dp2PxUtil.dip2px(12);
    /**
     * 顶部tips字体颜色
     */
    private int mTopFontColor = 0x333333;


    public int getFontSize() {
        return mFontSize;
    }

    public void setFontSize(int fontSize) {
        mFontSize = fontSize;
    }

    public int getFontColor() {
        return mFontColor;
    }

    public void setFontColor(int fontColor) {
        mFontColor = fontColor;
    }

    public int getLineSpace() {
        return mLineSpace;
    }

    public void setLineSpace(int lineSpace) {
        mLineSpace = lineSpace;
    }

    public int getTitleFontSize() {
        return mTitleFontSize;
    }

    public void setTitleFontSize(int titleFontSize) {
        mTitleFontSize = titleFontSize;
    }

    public int getParagraphSpace() {
        return mParagraphSpace;
    }

    public void setParagraphSpace(int paragraphSpace) {
        mParagraphSpace = paragraphSpace;
    }

    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        mPaddingLeft = paddingLeft;
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        mPaddingRight = paddingRight;
    }

    public int getPaddingTop() {
        return mPaddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        mPaddingTop = paddingTop;
    }

    public int getPaddingBottom() {
        return mPaddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        mPaddingBottom = paddingBottom;
    }

    public int getBottomHeight() {
        return mBottomHeight;
    }

    public void setBottomHeight(int bottomHeight) {
        mBottomHeight = bottomHeight;
    }

    public int getBottomFontSize() {
        return mBottomFontSize;
    }

    public void setBottomFontSize(int bottomFontSize) {
        mBottomFontSize = bottomFontSize;
    }

    public int getBottomFontColor() {
        return mBottomFontColor;
    }

    public void setBottomFontColor(int bottomFontColor) {
        mBottomFontColor = bottomFontColor;
    }

    public int getTopHeight() {
        return mTopHeight;
    }

    public void setTopHeight(int topHeight) {
        mTopHeight = topHeight;
    }

    public int getTopFontSize() {
        return mTopFontSize;
    }

    public void setTopFontSize(int topFontSize) {
        mTopFontSize = topFontSize;
    }

    public int getTopFontColor() {
        return mTopFontColor;
    }

    public void setTopFontColor(int topFontColor) {
        mTopFontColor = topFontColor;
    }
}
