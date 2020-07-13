package com.bubble.reader.page;

import com.bubble.reader.bean.ChapterBean;
import com.bubble.reader.bean.IChapter;
import com.bubble.reader.bean.PageResult;
import com.bubble.reader.chapter.listener.OnChapterResultListener;
import com.bubble.reader.chapter.listener.OnChapterRequestListener;
import com.bubble.reader.page.listener.PageListener;
import com.bubble.reader.widget.PageView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class OnlinePageCreator<T extends IChapter> extends PageCreator {

    /**
     * 默认预读一章
     */
    private static final int DEFAULT_PREPARE_CHAPTER_COUNT = 1;

    /**
     * 章节
     */
    private Map<String, IChapter> mChapters = new HashMap<>();
    /**
     * 当前章节
     */
    protected ChapterBean mCurrentChapter;
    /**
     * 在线阅读请求
     */
    private OnChapterRequestListener mOnlineRequestListener;
    /**
     * 当前下标
     */
    private int mCurrentIndex = 0;
    /**
     * 在线请求结果回调
     */
    private OnChapterResultListener<T> mOnlineChapterListener = new OnChapterResultListener<T>() {
        @Override
        public void onGetChapterSuccess(boolean isPrepare, T chapter) {
            mChapters.put("chapter" + chapter.getChapterNo(), chapter);
            if (!isPrepare) {
                notifyPage(PageListener.TYPE_BOOK_FINISHED);
            }
        }

        @Override
        public void onGetChapterFailure(String message) {

        }
    };
    /**
     * 预读章节数量
     */
    private int mPrepareCount = DEFAULT_PREPARE_CHAPTER_COUNT;
    /**
     * 章节总数量
     */
    private int mTotalChapterCount;

    protected OnlinePageCreator(PageView readView) {
        super(readView);
    }

    /*=======================================建造者=========================================*/
    public static class Builder extends PageCreator.Builder<OnlinePageCreator.Builder> {
        OnChapterRequestListener mOnlineRequestListener;

        public Builder(PageView view) {
            super(view);
        }

        @Override
        public <C extends PageCreator> C build() {
            OnlinePageCreator creator = new OnlinePageCreator(mReadView);
            creator.setOnlineRequestListener(mOnlineRequestListener);
            return (C) creator;
        }

        public OnlinePageCreator.Builder setOnlineRequestListener(OnChapterRequestListener onlineRequestListener) {
            mOnlineRequestListener = onlineRequestListener;
            return this;
        }
    }

    @Override
    public PageResult onNextPage() {
        // 从缓存中获取下一章
        IChapter chapter = mChapters.get("chapter" + mCurrentChapter.getChapterNo() + 1);
        // 如果下一章不为空
        if (chapter != null) {
            mPageResult.set(false, true);
        } else {
            // 下一章为空直接获取内容
            // 让外部获取内容 再通过mOnlineChapterListener 回调给自己
            mOnlineRequestListener.onRequest(false, mCurrentIndex, mOnlineChapterListener);
            mPageResult.set(true, false);
        }
        int startIndex = chapter == null ? mCurrentChapter.getChapterNo() + 1 : chapter.getChapterNo();
        // 预获取指定数量的章节
        for (int i = startIndex; i < startIndex + mPrepareCount; i++) {
            if (i < mTotalChapterCount && mChapters.get("chapter" + i) == null) {// 如果要获取的章节为空才获取
                // 让外部获取内容 再通过mOnlineChapterListener 回调给自己
                mOnlineRequestListener.onRequest(true, mCurrentIndex, mOnlineChapterListener);
            }
        }
        return mPageResult;
    }


    @Override
    public PageResult onPrePage() {
        // 让外部获取内容 再通过mOnlineChapterListener 回调给自己
        mOnlineRequestListener.onRequest(false, mCurrentIndex, mOnlineChapterListener);
        return mPageResult.set(false, false);
    }

    @Override
    protected void initData() {

    }

    public OnChapterRequestListener getOnlineRequestListener() {
        return mOnlineRequestListener;
    }

    public void setOnlineRequestListener(OnChapterRequestListener onlineRequestListener) {
        mOnlineRequestListener = onlineRequestListener;
    }
}
