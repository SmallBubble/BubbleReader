package com.bubble.reader.chapter;

import com.bubble.reader.chapter.listener.OnChapterListener;
import com.bubble.reader.chapter.listener.OnChapterResultListener;
import com.bubble.reader.bean.IChapter;
import com.bubble.reader.page.listener.PageListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 章节工厂
 */
public class ChapterFactory<T extends IChapter> implements IChapterFactory<T> {

    protected PageListener mPageListener;
    protected Map<String, T> mChapters = new HashMap<>();
    protected T mCurrentChapter;
    private OnChapterListener mOnChapterListener;
    protected OnChapterResultListener<T> mOnChapterResultListener = new OnChapterResultListener<T>() {
        @Override
        public void onGetChapterSuccess(boolean isPrepare, T chapter) {
            mChapters.put(chapter.getChapterName() + chapter.getChapterNo(), chapter);
            if (!isPrepare) {
                mOnChapterListener.onChapterLoaded();
            }
        }

        @Override
        public void onGetChapterFailure(String message) {
//            mOnChapterListener.on(message);
        }
    };


    @Override
    public int getCurrentChapterNo() {
        return mCurrentChapter.getChapterNo();
    }

    @Override
    public int getChapterCount() {
        return mCurrentChapter.getChapterCount();
    }

    @Override
    public int getLoadedChapterCount() {
        return mChapters.size();
    }

    @Override
    public Map<String, T> getLoadedChapters() {
        return mChapters;
    }

    @Override
    public void recycle() {

    }

    @Override
    public String getEncoding() {
        return null;
    }
}
