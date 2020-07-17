package com.bubble.breader.page;

import com.bubble.common.log.BubbleLog;
import com.bubble.breader.bean.IPage;
import com.bubble.breader.bean.ReadPage;
import com.bubble.breader.bean.PageResult;
import com.bubble.breader.chapter.ChapterFactory;
import com.bubble.breader.chapter.IChapterFactory;
import com.bubble.breader.chapter.TxtChapterFactory;
import com.bubble.breader.chapter.listener.OnChapterListener;
import com.bubble.breader.page.listener.PageListener;
import com.bubble.breader.utils.PageFactory;
import com.bubble.breader.widget.PageSettings;
import com.bubble.breader.widget.PageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bubble
 * @date 2020/6/20
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 离线内容 分页
 */
public abstract class PageCreator<T extends IPage> {
    private final static String TAG = PageCreator.class.getSimpleName();
    /**
     * 解析出来的页面
     */
    protected Map<String, T> mPages = new HashMap<>();
    /**
     * 可见页
     */
    protected ReadPage mVisiblePage;
    /**
     * 取消页
     */
    protected ReadPage mCancelPage;
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
    /**
     * 章节工厂
     */
    protected ChapterFactory mChapterFactory;
    /**
     * 页面参数设置 生成页面需要根据字体大小 行距等 获取内容
     */
    private PageSettings mSettings;

    protected PageCreator(PageView readView) {
        mPageView = readView;
    }

    /**
     * 通用數據初始化
     */
    public final void init() {
        if (mChapterFactory == null) {
            throw new RuntimeException("请设置章节工厂");
        }
        mSettings = mPageView.getSettings();
        mContentWidth = mPageView.getMeasuredWidth() - mSettings.getPaddingLeft() - mSettings.getPaddingRight();
        mContentHeight = mPageView.getMeasuredHeight() - mSettings.getTopHeight() - mSettings.getBottomHeight() - mSettings.getPaddingTop() - mSettings.getPaddingBottom();
        mPageResult = new PageResult();
        BubbleLog.e(TAG, mContentWidth + "   " + mContentHeight);

        mChapterFactory.addOnChapterListener(new OnChapterListener() {
            @Override
            public void onInitialized() {
                super.onInitialized();
                // 章节初始化完成
                onChapterInitialized();
            }

            @Override
            public void onChapterLoaded() {
                super.onChapterLoaded();
                onCurrentChapterLoaded();
            }
        });
        refreshPageConfig();
        mChapterFactory.init();
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

    public List<PageListener> getPageListeners() {
        return mPageListeners;
    }

    public void setPageListeners(List<PageListener> pageListeners) {
        mPageListeners = pageListeners;
    }

    public IChapterFactory getChapterFactory() {
        return mChapterFactory;
    }

    public void setChapterFactory(ChapterFactory chapterFactory) {
        mChapterFactory = chapterFactory;
    }

    public void onCancel() {

    }

    /**
     * 刷新页面
     */
    public void refreshPage() {
        //刷新配置信息
        refreshPageConfig();
        // 因为改变了配置 所有页面都会改变 所以清空已经生成的页面
        mPages.clear();
        // 通知生成页面
        onChapterInitialized();
    }

    /**
     * 刷新页面配置
     */
    private void refreshPageConfig() {
        PageFactory.getInstance()
                .height(mContentHeight)
                .width(mContentWidth)
                .lineSpace(mSettings.getLineSpace())
                .paragraphSpace(mSettings.getParagraphSpace())
                .fontSize(mSettings.getFontSize());
    }

    public static abstract class Builder<C extends PageCreator, T extends Builder> {
        protected PageView mReadView;
        protected ChapterFactory mChapterFactory;

        public Builder(PageView view) {
            mReadView = view;
        }

        /**
         * 创建一个创建者
         *
         * @return
         */
        public C build() {
            C pageCreator = onBuild();
            pageCreator.setChapterFactory(mChapterFactory);
            return pageCreator;
        }

        /**
         * 建造的时候子类回调 子类需要的信息在这个方法设置
         *
         * @return 返回一个页面生成器
         */
        protected abstract C onBuild();

        /**
         * 设置章节工厂
         *
         * @param factory 工厂
         * @return
         */
        public T chapterFactory(TxtChapterFactory factory) {
            mChapterFactory = factory;
            return (T) this;
        }
    }

    public void recycle() {
        if (mChapterFactory != null) {
            mChapterFactory.recycle();
        }
    }

    /*=====================================外部监听===================================*/

    public <T extends PageListener> void removePageListener(T listener) {
        mPageListeners.remove(listener);
    }

    public <T extends PageListener> void addPageListener(T pageListener) {
        mPageListeners.add(pageListener);
    }

    /**
     * 通知其他页面监听
     *
     * @param type
     */
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
                default:
            }
        }
    }

    /**
     * 章节初始化完成 加载完第一章
     * <p>
     * 改变页面配置的时候 也需要回调这个方法
     */
    public abstract void onChapterInitialized();

    /**
     * 当前章加载完成
     */
    public abstract void onCurrentChapterLoaded();

}
