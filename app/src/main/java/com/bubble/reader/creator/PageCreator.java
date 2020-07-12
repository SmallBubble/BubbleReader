package com.bubble.reader.creator;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.page.bean.PageResult;
import com.bubble.reader.page.listener.PageListener;
import com.bubble.reader.widget.PageView;

import java.util.ArrayList;
import java.util.List;

/**
 * packger：com.bubble.reader.page.offline
 * author：Bubble
 * date：2020/6/20
 * email：1337986595@qq.com
 * Desc：离线内容 分页
 */
public abstract class PageCreator {
    private final static String TAG = PageCreator.class.getSimpleName();
    /**
     * 阅读视图
     */
    protected PageView mReadView;
    /**
     * 可绘制高度
     */
    protected int mContentHeight;
    /**
     * 可绘制宽度
     */
    protected int mContentWidth;
    /**
     * 页宽 和页高 包含panding和margin
     */
    private int mPageWidth;
    private int mPageHeight;

    /**
     * 页边距
     */
    protected int mPadding = 0;
    /**
     * 页面监听
     */
    private List<PageListener> mPageListeners = new ArrayList<>();



    /**
     * 初始化 数据 子类重写
     */
    protected abstract void initData();

    /**
     * 获取下一页
     *
     * @return
     */
    public abstract PageResult onNextPage();

    /**
     * 获取下一页
     *
     * @return
     */
    public abstract PageResult onPrePage();

    /**
     * 阅读页返回结果
     */
    protected PageResult mPageResult;

    protected PageCreator(PageView readView) {
        mReadView = readView;
    }

    /**
     * 通用數據初始化
     */
    public final void init() {
        mPageResult = new PageResult();
        mPageWidth = mReadView.getMeasuredWidth();
        mPageHeight = mReadView.getMeasuredHeight();
        mContentWidth = mPageWidth - mPadding * 2;
        mContentHeight = mPageHeight - mPadding * 2;
        BubbleLog.e(TAG, mContentWidth + "   " + mContentHeight);
        initData();
    }

    public PageView getReadView() {
        return mReadView;
    }

    public void setReadView(PageView readView) {
        mReadView = readView;
    }

    public int getContentHeight() {
        return mContentHeight;
    }

    public void setContentHeight(int contentHeight) {
        mContentHeight = contentHeight;
    }

    public int getContentWidth() {
        return mContentWidth;
    }

    public void setContentWidth(int contentWidth) {
        mContentWidth = contentWidth;
    }

    public void onCancel() {

    }

    public static abstract class Builder<T extends Builder> {
        protected PageView mReadView;
        protected PageCreator mPageCreator;

        public Builder(PageView view) {
            mReadView = view;
        }

        /**
         * 创建一个创建者
         *
         * @return
         */
        public abstract <C extends PageCreator> C build();
    }

    public <T extends PageListener> void removePageListener(T listener) {
        mPageListeners.remove(listener);
    }

    public <T extends PageListener> void addPageListener(T pageListener) {
        mPageListeners.add(pageListener);
    }
}
