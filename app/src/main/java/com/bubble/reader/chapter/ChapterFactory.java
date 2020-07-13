package com.bubble.reader.chapter;

import com.bubble.reader.bean.IChapter;
import com.bubble.reader.chapter.listener.OnChapterListener;
import com.bubble.reader.chapter.listener.OnChapterRequestListener;
import com.bubble.reader.chapter.listener.OnChapterResultListener;

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

    protected Map<String, T> mChapters = new HashMap<>();
    protected T mCurrentChapter;
    protected OnChapterListener mOnChapterListener;
    protected boolean mInitialized = false;
    protected OnChapterRequestListener mOnChapterRequestListener;
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
            mOnChapterListener.onError(new Throwable(message));
        }
    };

    public OnChapterListener getOnChapterListener() {
        return mOnChapterListener;
    }

    public void setOnChapterListener(OnChapterListener onChapterListener) {
        mOnChapterListener = onChapterListener;
    }

    @Override
    public void initData() {

    }

    @Override
    public int getCurrentChapterNo() {
        return mCurrentChapter.getChapterNo();
    }

    @Override
    public String getCurrentContent() {
        return mCurrentChapter.getContent();
    }

    @Override
    public String getCurrentName() {
        return mCurrentChapter.getChapterName();
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
        return "UTF-8";
    }

    @Override
    public boolean isEnd() {
        return mCurrentChapter.getChapterCount() == mCurrentChapter.getChapterNo();
    }

    @Override
    public boolean isStart() {
        return mCurrentChapter.getChapterNo() == 1;
    }

    @Override
    public void onLoadChapter(boolean isNext) {
        if (isNext) {
            String key = mCurrentChapter.getChapterName() + (mCurrentChapter.getChapterNo() + 1);
            T t = mChapters.get(key);
            if (t == null) {
                // 获取到下一章为空 请求获取章节
                mOnChapterRequestListener.onRequest(false, mCurrentChapter.getChapterNo(), mOnChapterResultListener);
            }
        } else {
            String key = mCurrentChapter.getChapterName() + (mCurrentChapter.getChapterNo() - 1);
            T t = mChapters.get(key);
            if (t == null) {
                // 获取到下一章为空 请求获取章节
                mOnChapterRequestListener.onRequest(false, mCurrentChapter.getChapterNo(), mOnChapterResultListener);
            }
        }
    }
}
