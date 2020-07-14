package com.bubble.reader.page;

import android.graphics.Color;
import android.text.TextPaint;

import com.bubble.common.log.BubbleLog;
import com.bubble.common.utils.Dp2PxUtil;
import com.bubble.reader.bean.PageBean;
import com.bubble.reader.bean.PageResult;
import com.bubble.reader.chapter.TxtChapterFactory;
import com.bubble.reader.chapter.listener.OnChapterListener;
import com.bubble.reader.utils.PageFactory;
import com.bubble.reader.widget.PageSettings;
import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.impl.HorizontalMoveDrawHelper;
import com.bubble.reader.widget.draw.impl.HorizontalScrollDrawHelper;
import com.bubble.reader.widget.listener.OnContentListener;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class TxtPageCreatorV2 extends PageCreator {
    private static final String TAG = TxtPageCreatorV2.class.getSimpleName();
    /**
     * 解析出来的页面
     */
    private Map<String, PageBean> mPages = new HashMap<>();

    /**
     * 字体大小
     */
    private int mFontSize = Dp2PxUtil.dip2px(64);
    ;
    /**
     * 行距
     */
    private int mLineSpace = Dp2PxUtil.dip2px(12);

    /**
     * 画笔 用于测量文字
     */
    private TextPaint mPaint;
    /**
     * 段间距
     */
    private int mParagraphSpace = Dp2PxUtil.dip2px(30);

    private File mBookFile;
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

    private TxtChapterFactory mChapterFactory;
    private PageSettings mSettings;

    public TxtPageCreatorV2(PageView readView) {
        super(readView);
    }

    @Override
    protected void initData() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(Color.RED);
        mPaint.setSubpixelText(true);
        mChapterFactory = new TxtChapterFactory();
        mChapterFactory.setBookFile(mBookFile);
        mSettings = mPageView.getSettings();
        PageFactory.getInstance()
                .height(mContentHeight)
                .width(mContentWidth)
                .lineSpace(mSettings.getLineSpace())
                .paragraphSpace(mSettings.getParagraphSpace())
                .fontSize(mSettings.getFontSize());

        mChapterFactory.setOnChapterListener(new OnChapterListener() {
            @Override
            public void onInitialized() {
                String content = mChapterFactory.getCurrentContent();
                // 从工厂生成当前章节的页面
                List<PageBean> pages = PageFactory.getInstance()
                        .setEncoding(getEncoding())
                        .createPages(mChapterFactory.getCurrentName()
                                , mChapterFactory.getCurrentChapterNo()
                                , content);
                mVisiblePage = pages.get(0);
                mPageView.getCurrentPage().setPageBean(mVisiblePage);
                mPages.putAll(PageFactory.getInstance().convertToMap(pages));
            }

            @Override
            public void onChapterLoaded() {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
        // 开始解析书籍
        mChapterFactory.initData();
    }

    /*=======================================建造者=========================================*/
    public static class Builder extends PageCreator.Builder<Builder> {
        private File mFile;

        public Builder(PageView view) {
            super(view);
        }

        @Override
        public <C extends PageCreator> C build() {
            TxtPageCreatorV2 creator = new TxtPageCreatorV2(mReadView);
            creator.setBookFile(mFile);
            return (C) creator;
        }

        public Builder file(String path) {
            return file(new File(path));
        }

        public Builder file(File file) {
            mFile = file;
            return this;
        }

    }

    /*=======================================私有方法阅读=========================================*/

    /**
     * 设置要阅读的文件
     *
     * @param bookFile
     * @version v1 只支持txt
     */
    private void setBookFile(File bookFile) {
        mBookFile = bookFile;
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
    public PageResult onNextPage() {
        // 最后一章最后一页
        if (mChapterFactory.isBookEnd() && mVisiblePage.getPageNum() == mVisiblePage.getPageCount()) {
            return mPageResult.set(false, false);
        }
        mCancelPage = mVisiblePage;
        //该章节最后一页 获取下一章内容
        if (mVisiblePage.getPageCount() == mVisiblePage.getPageNum()) {
            // 通知章节工厂 获取下一章
            mChapterFactory.onLoadChapter(true);
            // 获取当前章内容（上一步获取了下一章 下一章变为当前章）
            String content = mChapterFactory.getCurrentContent();
            // 从工厂生成当前章节的页面
            List<PageBean> pages = PageFactory.getInstance()
                    .createPages(mChapterFactory.getCurrentName()
                            , mChapterFactory.getCurrentChapterNo()
                            , content);

            mPageView.getCurrentPage().setPageBean(mVisiblePage);
            mVisiblePage = pages.get(0);
            mPageView.getNextPage().setPageBean(mVisiblePage);
            mPages.putAll(PageFactory.getInstance().convertToMap(pages));
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
            mChapterFactory.onLoadChapter(false);
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
            return mPageResult.set(true, true);
        } else {
            // 有下一章但是需要一段时间加载
            return mPageResult.set(true, false);
        }
    }
}
