package com.bubble.reader.page;

import android.text.TextUtils;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.bean.PageBean;
import com.bubble.reader.bean.PageResult;
import com.bubble.reader.page.listener.PageListener;
import com.bubble.reader.utils.PageFactory;
import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.impl.HorizontalMoveDrawHelper;
import com.bubble.reader.widget.draw.impl.HorizontalScrollDrawHelper;
import com.bubble.reader.widget.listener.OnContentListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 页面生成器(默认的) 可以自己继承{@link PageCreator } 实现自己的生成器
 */
public class DefaultPageCreator extends PageCreator {
    private static final String TAG = DefaultPageCreator.class.getSimpleName();
    /**
     * 解析出来的页面
     */
    private Map<String, PageBean> mPages = new HashMap<>();


    /**
     * 可见页
     */
    private PageBean mVisiblePage;
    /**
     * 不可见页
     */
    private PageBean mInvisiblePage;

    /**
     * 取消页
     */
    private PageBean mCancelPage;

    public DefaultPageCreator(PageView readView) {
        super(readView);
    }

    @Override
    protected void initData() {
    }

    /*=======================================建造者=========================================*/

    public static class Builder extends PageCreator.Builder<DefaultPageCreator, Builder> {

        public Builder(PageView view) {
            super(view);
        }

        @Override
        protected DefaultPageCreator onBuild() {
            DefaultPageCreator creator = new DefaultPageCreator(mReadView);
            return creator;
        }
    }

    /*=======================================私有方法阅读=========================================*/


    /*=======================================对外方法阅读=========================================*/

    /**
     * 获取编码
     *
     * @return
     */
    public String getEncoding() {
        return mChapterFactory.getEncoding();
    }

    /**
     * 取消翻页
     * 回复原来的页面
     * 以下两个帮助类中 会通过 {@link OnContentListener#onCancel()}中的监听回调 调用该方法
     * <p>
     * {@link HorizontalMoveDrawHelper }
     * <p>
     * {@link HorizontalScrollDrawHelper}
     */
    @Override
    public void onCancel() {
        super.onCancel();
        mVisiblePage = mCancelPage;
        BubbleLog.e(TAG, "mCancel ====  drawStatic");
    }

    @Override
    public void onChapterInitialized() {
        String content = mChapterFactory.getCurrentContent();
        // 从工厂生成当前章节的页面
        List<PageBean> pages = PageFactory.getInstance()
                .setEncoding(getEncoding())
                .createPages(mChapterFactory.getCurrentName()
                        , mChapterFactory.getCurrentChapterNo()
                        , content);
        mVisiblePage = pages.get(0);
        mPageView.getCurrentPage().setPageBean(mVisiblePage);
        mPageView.getNextPage().setPageBean(mVisiblePage);

        mPages.putAll(PageFactory.getInstance().convertToMap(pages));
        // 这里初始化成功后 通知要监听的地方
        notifyPage(PageListener.TYPE_PAGE_LOAD_FINISHED);
    }

    @Override
    public void onCurrentChapterLoaded() {

    }

    @Override
    public PageResult onNextPage() {
        // 最后一章最后一页
        if (mChapterFactory.isBookEnd() && mVisiblePage.getPageNum() == mVisiblePage.getPageCount()) {
            return mPageResult.set(false, false);
        }

        mCancelPage = mVisiblePage;
        //该章节最后一页 获取下一章内容
        if (mVisiblePage.getPageCount() == mVisiblePage.getPageNum()) {
            // 通知章节工厂 获取下一章
            mChapterFactory.loadChapter(true);
            // 获取当前章内容（上一步获取了下一章 下一章变为当前章）
            String content = mChapterFactory.getCurrentContent();
            if (TextUtils.isEmpty(content)) {
                // 本章内容为空
                // 获取本章内容
                mChapterFactory.loadChapter();

            } else {
                // 从工厂生成当前章节的页面
                List<PageBean> pages = PageFactory.getInstance()
                        .createPages(mChapterFactory.getCurrentName()
                                , mChapterFactory.getCurrentChapterNo()
                                , content);

                mPageView.getCurrentPage().setPageBean(mVisiblePage);
                mVisiblePage = pages.get(0);
                mPageView.getNextPage().setPageBean(mVisiblePage);
                mPages.putAll(PageFactory.getInstance().convertToMap(pages));
            }
        } else {
            // 直接获取Map里面的
            String key = PageFactory.getInstance().getKey(mChapterFactory.getCurrentName(), mChapterFactory.getCurrentChapterNo(), mVisiblePage.getPageNum() + 1);
            mVisiblePage = mPages.get(key);
            mPageView.getNextPage().setPageBean(mVisiblePage);
        }
        return mPageResult.set(true, true);
    }

    @Override
    public PageResult onPrePage() {
        // 第一章 第一页
        if (mChapterFactory.isStart() && mVisiblePage.getPageNum() == 1) {
            return mPageResult.set(false, false);
        }
        // 当前是章节第一页 获取上一章内容
        if (mVisiblePage.getPageNum() == 1) {
            // 通知章节工厂 获取上一章
            mChapterFactory.loadChapter(false);
            // 获取当前章内容（上一步获取了上一章 上一章变为当前章）
            String content = mChapterFactory.getCurrentContent();
            // 从工厂生成当前章节的页面
            List<PageBean> pages = PageFactory.getInstance()
                    .createPages(mChapterFactory.getCurrentName()
                            , mChapterFactory.getCurrentChapterNo()
                            , content);
            // 获取最后一页

            mPageView.getCurrentPage().setPageBean(mVisiblePage);
            mVisiblePage = pages.get(pages.size() - 1);
            mPageView.getNextPage().setPageBean(mVisiblePage);
            mPages.putAll(PageFactory.getInstance().convertToMap(pages));
        } else {
            // 直接获取Map里面的
            String key = PageFactory.getInstance().getKey(mChapterFactory.getCurrentName(), mChapterFactory.getCurrentChapterNo(), mVisiblePage.getPageNum() - 1);
            mVisiblePage = mPages.get(key);
            mPageView.getNextPage().setPageBean(mVisiblePage);
        }
        if (mVisiblePage != null) {
            // 直接生成 有下一章 已经加载完成
            return mPageResult.set(true, true);
        } else {
            // 有下一章但是需要一段时间加载
            return mPageResult.set(true, false);
        }
    }

}
