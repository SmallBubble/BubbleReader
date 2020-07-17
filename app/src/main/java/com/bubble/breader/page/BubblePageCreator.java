package com.bubble.breader.page;

import android.graphics.Canvas;
import android.text.TextUtils;

import com.bubble.common.log.BubbleLog;
import com.bubble.breader.bean.ReadPage;
import com.bubble.breader.bean.PageResult;
import com.bubble.breader.page.listener.PageListener;
import com.bubble.breader.utils.PageFactory;
import com.bubble.breader.widget.PageView;
import com.bubble.breader.widget.draw.impl.HorizontalMoveDrawHelper;
import com.bubble.breader.widget.draw.impl.HorizontalScrollDrawHelper;
import com.bubble.breader.widget.listener.OnContentListener;

import java.util.List;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 页面生成器(默认的) 可以自己继承{@link PageCreator } 实现自己的生成器
 * 加上其他内容 例如加上图片大小 然后再{@link com.bubble.breader.widget.draw.base.DrawHelper#draw(Canvas)} 中绘制对应内容
 */
public class BubblePageCreator extends PageCreator<ReadPage> {
    private static final String TAG = BubblePageCreator.class.getSimpleName();


    public BubblePageCreator(PageView readView) {
        super(readView);
    }

    @Override
    protected void initData() {
    }

    /*=======================================建造者=========================================*/

    public static class Builder extends PageCreator.Builder<BubblePageCreator, Builder> {

        public Builder(PageView view) {
            super(view);
        }

        @Override
        protected BubblePageCreator onBuild() {
            BubblePageCreator creator = new BubblePageCreator(mReadView);
            return creator;
        }
    }

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
        List<ReadPage> pages = PageFactory.getInstance()
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
        /* *****以下内容表示有下一章 但是是否能直接获取 要根据具体情况 有可能需要加载后才能获取******* */
        mCancelPage = mVisiblePage;
        //该章节最后一页 获取下一章内容
        if (mVisiblePage.getPageCount() == mVisiblePage.getPageNum()) {
            // 通知章节工厂 获取下一章
            mChapterFactory.loadChapter(true);
            // 获取当前章内容（上一步获取了下一章 下一章变为当前章）
            String content = mChapterFactory.getCurrentContent();
            if (TextUtils.isEmpty(content)) {
                // 获取不到 下一章的内容 需要从网络或者文件中重新读取
                // 获取本章内容
                mChapterFactory.loadChapter();
                // 返回结果 有下一章 但是需要时间加载
                return mPageResult.set(true, false);
            } else {
                //能获取到内容 从工厂生成当前章节的页面
                List<ReadPage> pages = PageFactory.getInstance()
                        .createPages(mChapterFactory.getCurrentName()
                                , mChapterFactory.getCurrentChapterNo()
                                , content);

                mPageView.getCurrentPage().setPageBean(mVisiblePage);
                mVisiblePage = pages.get(0);
                mPageView.getNextPage().setPageBean(mVisiblePage);
                // 缓存该章节的页面 下次如果没有切换章节 直接从map中读取 不用再次解析
                mPages.putAll(PageFactory.getInstance().convertToMap(pages));
            }
        } else {
            //不是最后一页 已经解析过了 直接获取Map里面的
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
        mCancelPage = mVisiblePage;
        // 当前是章节第一页 获取上一章内容
        if (mVisiblePage.getPageNum() == 1) {
            // 通知章节工厂 获取上一章
            mChapterFactory.loadChapter(false);
            // 获取当前章内容（上一步获取了上一章 上一章变为当前章）
            String content = mChapterFactory.getCurrentContent();
            // 从工厂生成当前章节的页面
            List<ReadPage> pages = PageFactory.getInstance()
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