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
public class ChapterFactory<T extends IChapter> implements IChapterFactory<T>, OnChapterResultListener<T> {
    /**
     * 章节
     */
    protected Map<String, T> mChapters = new HashMap<>();
    /**
     * 当前章节
     */
    protected T mCurrentChapter;
    /**
     * 本书是否初始化
     */
    protected boolean mInitialized = false;
    /**
     * 预加载数量
     */
    private int mPrepareCount;
    /**
     * 章节监听
     */
    protected OnChapterListener mOnChapterListener;
    /**
     * 章节请求监听 提供给子类或者外部实现
     */
    protected OnChapterRequestListener<T> mOnChapterRequestListener;
    /**
     * 章节请求结果监听
     */
    protected OnChapterResultListener<T> mOnChapterResultListener;

    /*****************************************监听*****************************************/
    public OnChapterListener getOnChapterListener() {
        return mOnChapterListener;
    }

    public void setOnChapterListener(OnChapterListener onChapterListener) {
        mOnChapterListener = onChapterListener;
    }

    /*****************************************初始化*****************************************/
    @Override
    public void initData() {
        mOnChapterResultListener = this;
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
    public String getEncoding() {
        return "UTF-8";
    }

    @Override
    public boolean isBookEnd() {
        return mCurrentChapter.getChapterCount() == mCurrentChapter.getChapterNo();
    }

    @Override
    public boolean isStart() {
        return mCurrentChapter.getChapterNo() == 1;
    }

    @Override
    public void recycle() {
    }

    @Override
    public boolean loadChapter(boolean isNext) {
        if (isNext) {
            // 不过是往前还是往后 都会预读指定数量的章节
            prepareRequest();
            // 是往后翻页 先从内存中获取 已经预读的章节
            T t = mChapters.get(getKey(mCurrentChapter.getChapterNo() + 1));
            if (t == null) {
                // 如果预读里面没有章节就重新获取
                // 获取到下一章为空 请求获取章节
                mOnChapterRequestListener.onRequest(false, mCurrentChapter.getChapterNo() + 1, mOnChapterResultListener);
                return false;
            } else {
                mCurrentChapter = t;
                return true;
            }
        } else {
            // 不过是往前还是往后 都会预读指定数量的章节
            prepareRequest();
            // 如果往前阅读 也是先获取预读章节
            T t = mChapters.get(getKey(mCurrentChapter.getChapterNo() - 1));
            if (t == null) {
                // 没有就重新获取
                // 获取到下一章为空 请求获取章节
                mOnChapterRequestListener.onRequest(false, mCurrentChapter.getChapterNo() - 1, mOnChapterResultListener);
                return false;
            } else {
                mCurrentChapter = t;
                return true;
            }
        }

    }

    private void prepareRequest() {
        for (int i = 0; i < mPrepareCount; i++) {
            // 如果已经预读到了 就不会重新预读了
            if (mChapters.get(getKey(mCurrentChapter.getChapterNo())) != null) {
                continue;
            }
            mOnChapterRequestListener.onRequest(true, mCurrentChapter.getChapterNo() + i, mOnChapterResultListener);
        }
    }

    @Override
    public void loadChapter() {
        // 获取到下一章为空 请求获取章节
        mOnChapterRequestListener.onRequest(false, mCurrentChapter.getChapterNo(), mOnChapterResultListener);
    }


    /*****************************************辅助方法*****************************************/
    protected String getKey(int chapterNo) {
        return "chapter" + chapterNo;
    }

    /*****************************************请求结果*****************************************/
    @Override
    public void onGetChapterSuccess(boolean isPrepare, T chapter) {
        mChapters.put(getKey(chapter.getChapterNo()), chapter);
        if (!isPrepare) {
            // 如果不是预读 就通知监听这 章节加载完成 可以完成其他操作 比如分页/绘制内容等
            mOnChapterListener.onChapterLoaded();
        }
    }

    @Override
    public void onGetChapterFailure(boolean isPrepare, String message) {
        if (!isPrepare) {
            mOnChapterListener.onError(new Throwable(message));
        }
    }
}
