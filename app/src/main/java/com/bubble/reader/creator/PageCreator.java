package com.bubble.reader.creator;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.page.listener.PageListener;
import com.bubble.reader.widget.PageView;

import java.util.ArrayList;
import java.util.List;

/**
 * packger：com.bubble.reader.page.offline
 * auther：Bubble
 * date：2020/6/20
 * email：1337986595@qq.com
 * Desc：离线内容 分页
 */
public abstract class PageCreator {
    private final static String TAG = PageCreator.class.getSimpleName();
    protected PageView mReadView;

    protected PageCreator(PageView readView) {
        mReadView = readView;
    }

    /**
     * 通用數據初始化
     */
    public final void init() {
        mPageWidth = mReadView.getMeasuredWidth() - mPadding * 2;
        mPageHeight = mReadView.getMeasuredHeight() - mPadding * 2;
        BubbleLog.e(TAG, mPageWidth + "   " + mPageHeight);
        initData();
    }

    /**
     * 初始化 数据 子类重写
     */
    protected abstract void initData();

    /**
     * 可绘制高度
     */
    protected int mPageHeight;
    /**
     * 可绘制宽度
     */
    protected int mPageWidth;

    /**
     * 页边距
     */
    protected int mPadding = 100;

    protected List<PageListener> mPageListeners = new ArrayList<>();

    /**
     * 获取下一页
     *
     * @return
     */
    public abstract boolean onNextPage();

    /**
     * 获取下一页
     *
     * @return
     */
    public abstract boolean onPrePage();

    public PageView getReadView() {
        return mReadView;
    }

    public void setReadView(PageView readView) {
        mReadView = readView;
    }

    public int getPageHeight() {
        return mPageHeight;
    }

    public void setPageHeight(int pageHeight) {
        mPageHeight = pageHeight;
    }

    public int getPageWidth() {
        return mPageWidth;
    }

    public void setPageWidth(int pageWidth) {
        mPageWidth = pageWidth;
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
