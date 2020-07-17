package com.bubble.breader.widget;

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
    public interface OnSettingListener {
        /**
         * 发生改变
         */
        void onChanged();
    }

    private OnSettingListener mOnSettingListener;

    public PageSettings(OnSettingListener onSettingListener) {
        mOnSettingListener = onSettingListener;
    }


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
    private int mTitleFontSize = 72;
    /**
     * 标题文字颜色
     */
    private int mTitleFontColor = Color.parseColor("#ff0000");
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
    private int mBottomHeight = 50;
    /**
     * 顶部字体大小
     */
    private int mBottomFontSize = Dp2PxUtil.sp2px(24);
    /**
     * 顶部tips字体颜色
     */
    private int mBottomFontColor = Color.parseColor("#333333");

    /**
     * 顶部区域
     */
    private int mTopHeight = 100;

    /**
     * 顶部字体大小
     */
    private int mTopFontSize = Dp2PxUtil.sp2px(12);
    /**
     * 顶部tips字体颜色
     */
    private int mTopFontColor = 0x333333;

    /**
     * 是否显示顶部tips
     */
    private Boolean mShowTop = true;
    /**
     * 是否显示底部tips
     */
    private Boolean mShowBottom = true;
    /**
     * 底部和顶部时候滑动(上下滑动的时候回禁用滑动 底部和顶部不会跟随页面滑动)
     */
    private Boolean mBottomAndTopScrollEnable;

    public Boolean getBottomAndTopScrollEnable() {
        return mBottomAndTopScrollEnable;
    }

    public void setBottomAndTopScrollEnable(Boolean bottomAndTopScrollEnable) {
        if (mBottomAndTopScrollEnable.equals(bottomAndTopScrollEnable)) {
            mBottomAndTopScrollEnable = bottomAndTopScrollEnable;
            mOnSettingListener.onChanged();
        }
    }

    public Boolean isShowTop() {
        return mShowTop;
    }

    public void setShowTop(Boolean showTop) {
        if (mShowTop.equals(showTop)) {
            mShowTop = showTop;
            mOnSettingListener.onChanged();
        }
    }

    public Boolean isShowBottom() {
        return mShowBottom;
    }

    public void setShowBottom(Boolean showBottom) {
        if (mShowBottom.equals(showBottom)) {
            mShowBottom = showBottom;
            mOnSettingListener.onChanged();
        }
    }

    public int getFontSize() {
        return mFontSize;
    }

    public void setFontSize(int fontSize) {
        if (mFontSize != fontSize) {
            mFontSize = fontSize;
            mOnSettingListener.onChanged();
        }
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
        if (mLineSpace != lineSpace) {
            mLineSpace = lineSpace;
            mOnSettingListener.onChanged();
        }
    }

    public int getTitleFontSize() {
        return mTitleFontSize;
    }

    public int getTitleFontColor() {
        return mTitleFontColor;
    }

    public void setTitleFontColor(int titleFontColor) {
        mTitleFontColor = titleFontColor;
    }

    public void setTitleFontSize(int titleFontSize) {
        if (mTitleFontSize != titleFontSize) {
            mTitleFontSize = titleFontSize;
            mOnSettingListener.onChanged();
        }
    }

    public int getParagraphSpace() {
        return mParagraphSpace;
    }

    public void setParagraphSpace(int paragraphSpace) {
        if (mParagraphSpace != paragraphSpace) {
            mParagraphSpace = paragraphSpace;
            mOnSettingListener.onChanged();
        }
    }

    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        if (mPaddingLeft != paddingLeft) {
            mPaddingLeft = paddingLeft;
            mOnSettingListener.onChanged();
        }
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        if (mPaddingRight != paddingRight) {
            mPaddingRight = paddingRight;
            mOnSettingListener.onChanged();
        }
    }

    public int getPaddingTop() {
        return mPaddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        if (mPaddingTop != paddingTop) {
            mPaddingTop = paddingTop;
            mOnSettingListener.onChanged();
        }
    }

    public int getPaddingBottom() {
        return mPaddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        if (paddingBottom != mPaddingBottom) {
            mPaddingBottom = paddingBottom;
            mOnSettingListener.onChanged();
        }
    }

    public int getBottomHeight() {
        return mBottomHeight;
    }

    public void setBottomHeight(int bottomHeight) {
        if (mBottomHeight != bottomHeight) {
            mBottomHeight = bottomHeight;
            mOnSettingListener.onChanged();
        }
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
        if (topHeight != mTopHeight) {
            mTopHeight = topHeight;
            mOnSettingListener.onChanged();
        }
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
