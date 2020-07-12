package com.bubble.reader.page;

import com.bubble.reader.creator.PageCreator;
import com.bubble.reader.page.bean.ChapterBean;
import com.bubble.reader.page.bean.PageResult;
import com.bubble.reader.page.listener.OnlineChapterListener;
import com.bubble.reader.page.listener.OnlineRequestListener;
import com.bubble.reader.widget.PageView;

import java.io.File;
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
public class OnlinePageCreator extends PageCreator {
    //    private List<ChapterBean> mChapters = new ArrayList<>();
    private Map<String, ChapterBean> mChapters = new HashMap<>();

    protected ChapterBean mCurrentChapter;

    private OnlineRequestListener mOnlineRequestListener;

    private int mCurrentIndex = 0;
    private OnlineChapterListener mOnlineChapterListener = new OnlineChapterListener() {
        @Override
        public void onNextChapter(ChapterBean chapter) {
            mChapters.put("chapter" + chapter.getChapterIndex(), chapter);
        }

        @Override
        public void onPreChapter(ChapterBean chapter) {
            mChapters.put("chapter" + chapter.getChapterIndex(), chapter);
        }
    };
    /**
     * 预读章节数量
     */
    private int mPrepareCount = 1;

    protected OnlinePageCreator(PageView readView) {
        super(readView);
    }

    /*=======================================建造者=========================================*/
    public static class Builder extends PageCreator.Builder<OnlinePageCreator.Builder> {
        private File mFile;

        public Builder(PageView view) {
            super(view);
        }

        @Override
        public <C extends PageCreator> C build() {
            OnlinePageCreator creator = new OnlinePageCreator(mReadView);
            return (C) creator;
        }

        public OnlinePageCreator.Builder file(String path) {
            return file(new File(path));
        }

        public OnlinePageCreator.Builder file(File file) {
            mFile = file;
            return this;
        }

    }

    @Override
    public PageResult onNextPage() {
        // 从缓存中获取下一章
        ChapterBean chapter = mChapters.get("chapter" + mCurrentChapter.getChapterIndex() + 1);
        // 如果下一章不为空
        if (chapter != null) {
            mPageResult.set(false, true);
        } else {
            // 下一章为空直接获取内容
            // 让外部获取内容 再通过mOnlineChapterListener 回调给自己
            mOnlineRequestListener.getNextChapter(mCurrentIndex, mOnlineChapterListener);
            mPageResult.set(true, false);
        }
        int startIndex = chapter == null ? mCurrentChapter.getChapterIndex() + 1 : chapter.getChapterIndex();
        // 预获取指定数量的章节
        for (int i = startIndex; i < startIndex + mPrepareCount; i++) {
            if (mChapters.get("chapter" + i) == null) {// 如果要获取的章节为空才获取
                // 让外部获取内容 再通过mOnlineChapterListener 回调给自己
                mOnlineRequestListener.getNextChapter(mCurrentIndex, mOnlineChapterListener);
            }
        }
        return mPageResult;
    }


    @Override
    public PageResult onPrePage() {


        // 让外部获取内容 再通过mOnlineChapterListener 回调给自己
        mOnlineRequestListener.getPreChapter(mCurrentIndex, mOnlineChapterListener);
        return mPageResult.set(false, false);
    }

    @Override
    protected void initData() {

    }

    public OnlineRequestListener getOnlineRequestListener() {
        return mOnlineRequestListener;
    }

    public void setOnlineRequestListener(OnlineRequestListener onlineRequestListener) {
        mOnlineRequestListener = onlineRequestListener;
    }
}
