package com.bubble.reader.page;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.bean.PageResult;
import com.bubble.reader.page.listener.PageListener;
import com.bubble.reader.widget.PageSettings;
import com.bubble.reader.widget.PageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bubble
 * @date 2020/6/20
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 离线内容 分页
 */
public abstract class PageCreator {
    private final static String TAG = PageCreator.class.getSimpleName();
    /**
     * 阅读视图
     */
    protected PageView mPageView;
    /**
     * 可绘制高度
     */
    protected int mContentHeight;
    /**
     * 可绘制宽度
     */
    protected int mContentWidth;
    /**
     * 页面监听
     */
    protected List<PageListener> mPageListeners = new ArrayList<>();

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
        mPageView = readView;
    }

    /**
     * 通用數據初始化
     */
    public final void init() {
        mPageResult = new PageResult();
        PageSettings settings = mPageView.getSettings();
        mContentWidth = mPageView.getMeasuredWidth() - settings.getPaddingLeft() - settings.getPaddingRight();
        mContentHeight = mPageView.getMeasuredHeight() - settings.getTopHeight() - settings.getBottomHeight() - settings.getPaddingTop() - settings.getPaddingBottom();
        BubbleLog.e(TAG, mContentWidth + "   " + mContentHeight);
        initData();
    }

    public PageView getPageView() {
        return mPageView;
    }

    public void setPageView(PageView pageView) {
        mPageView = pageView;
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

    protected void notifyPage(int type) {
        for (PageListener listener : mPageListeners) {
            switch (type) {
                case PageListener.TYPE_PAGE_LOAD_FINISHED:
                    listener.onPageLoadFinished();
                    break;
                case PageListener.TYPE_BOOK_START:
                    listener.onBookStart();
                    break;
                case PageListener.TYPE_BOOK_FINISHED:
                    listener.onBookFinished();
                    break;
                case PageListener.TYPE_ERROR:
                    listener.onError("加载失败");
                    break;
                case PageListener.TYPE_SUCCESS:
                    listener.onSuccess();
                    break;
            }
        }
    }
}
