package com.bubble.breader.chapter;

import com.bubble.breader.bean.IChapter;
import com.bubble.breader.chapter.listener.OnChapterListener;
import com.bubble.breader.chapter.loader.ChapterLoader;
import com.bubble.breader.chapter.loader.ChaptersLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private int mPrepareCount = 1;

    private boolean mCacheEnable;

    private List<OnChapterListener> mOnChapterListeners = new ArrayList<>();

    private ChapterLoader<T> mChapterLoader;

    private ChaptersLoader<T> mCacheLoader;

    /*****************************************加载结果*****************************************/
    private ChaptersLoader.ChaptersResult<T> mCacheResult = new ChaptersLoader.ChaptersResult<T>() {
        @Override
        public void onResult(Map<String, T> chapters) {
            if (chapters == null || chapters.isEmpty()) {
                onInitData();
            } else {
                mCurrentChapter = chapters.get(0);
                notifyChapter(OnChapterListener.TYPE_INIT);
                mChapters.putAll(chapters);
            }
        }
    };
    private ChapterLoader.ChapterResult<T> mChapterResult = new ChapterLoader.ChapterResult<T>() {
        @Override
        public void onResult(boolean isPrepare, T chapter) {
            // 存起来
            mChapters.put(getKey(chapter.getChapterNo()), chapter);
            if (!isPrepare) {//预加载的
                notifyChapter(OnChapterListener.TYPE_LOADED);
            } else {
                notifyChapter(OnChapterListener.TYPE_PREPARE_LOAD);
            }
        }
    };

    /*****************************************监听*****************************************/

    protected void notifyChapter(int type, Throwable e) {
        for (OnChapterListener listener : mOnChapterListeners) {
            switch (type) {
                case OnChapterListener.TYPE_INIT:
                    listener.onInitialized();
                    break;
                case OnChapterListener.TYPE_LOADED:
                    listener.onChapterLoaded();
                    break;
                case OnChapterListener.TYPE_COMPLETE:
                    listener.onComplete();
                    break;
                case OnChapterListener.TYPE_ERROR:
                    listener.onError(e);
                    break;
                case OnChapterListener.TYPE_PREPARE_LOAD:
                    listener.onPrepareComplete();
                    break;
                default:
            }
        }
    }

    protected void notifyChapter(int type) {
        notifyChapter(type, null);
    }

    public void removeOnChapterListener(OnChapterListener onChapterListener) {
        mOnChapterListeners.remove(onChapterListener);
    }

    public void addOnChapterListener(OnChapterListener onChapterListener) {
        mOnChapterListeners.add(onChapterListener);
    }

    public ChapterLoader<T> getChapterLoader() {
        return mChapterLoader;
    }

    public void setChapterLoader(ChapterLoader<T> chapterLoader) {
        mChapterLoader = chapterLoader;
    }

    public ChaptersLoader<T> getCacheLoader() {
        return mCacheLoader;
    }

    public void setCacheLoader(ChaptersLoader<T> cacheLoader) {
        mCacheLoader = cacheLoader;
    }

    public void setCacheEnable(boolean cacheEnable) {
        mCacheEnable = cacheEnable;
    }

    public boolean getCacheEnable() {
        return mCacheEnable;
    }

    /*****************************************初始化*****************************************/
    @Override
    public final void init() {
        //开启缓存 先加载缓存数据
        if (mCacheEnable) {
            if (mCacheLoader == null) {
                throw new RuntimeException("开启缓存 必须设置缓存加载器");
            }
            mCacheLoader.loadCache(mCacheResult);
        } else {
            // 没开启 直接获取新数据
            onInitData();
        }
    }

    public void setPrepareCount(int prepareCount) {
        mPrepareCount = prepareCount;
    }

    protected void onInitData() {
        if (mChapterLoader == null) {
            throw new RuntimeException("请设置章节加载器");
        }
        // 初始化数据 加载第1章
        mChapterLoader.loadChapter(false, 1, mChapterResult);
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
        if (mCurrentChapter == null) return true;
        return mCurrentChapter.getChapterCount() == mCurrentChapter.getChapterNo();
    }

    @Override
    public boolean isStart() {
        return mCurrentChapter.getChapterNo() == 1;
    }

    @Override
    public void recycle() {
        if (mChapterLoader != null) {
            mChapterLoader.recycle();
        }
        if (mCacheLoader != null) {
            mCacheLoader.recycle();
        }
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
                mChapterLoader.loadChapter(false, mCurrentChapter.getChapterNo() + 1, mChapterResult);
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
                mChapterLoader.loadChapter(false, mCurrentChapter.getChapterNo() - 1, mChapterResult);
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
            mChapterLoader.loadChapter(true, mCurrentChapter.getChapterNo() + i, mChapterResult);
        }
    }

    @Override
    public void loadChapter() {
        // 获取到下一章为空 请求获取章节
        mChapterLoader.loadChapter(false, mCurrentChapter.getChapterNo(), mChapterResult);
    }


    /*****************************************辅助方法*****************************************/
    protected String getKey(int chapterNo) {
        return "chapter" + chapterNo;
    }

    /*****************************************建造起*****************************************/
    public static abstract class Builder<T extends ChapterFactory.Builder> {

        public Builder() {

        }

        /**
         * 创建一个创建者
         *
         * @return
         */
        public abstract <C extends ChapterFactory> C build();
    }
}
