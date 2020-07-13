package com.bubble.reader.bean;

/**
 * @author Bubble
 * @date 2020/7/10
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class PageResult {
    /**
     * 是否有下一章
     */
    private boolean mHasNext;
    /**
     * 是否需要等待加载
     */
    private boolean mLoading;

    public PageResult(boolean hasNext, boolean loading) {
        mHasNext = hasNext;
        mLoading = loading;
    }

    public PageResult set(boolean hasNext, boolean loading) {
        mHasNext = hasNext;
        mLoading = loading;
        return this;
    }

    public PageResult() {
    }


    public boolean isHasNext() {
        return mHasNext;
    }

    public void setHasNext(boolean hasNext) {
        mHasNext = hasNext;
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "mHasNext=" + mHasNext +
                ", mLoading=" + mLoading +
                '}';
    }
}
