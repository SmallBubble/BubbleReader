package com.bubble.reader.chapter.listener;

import com.bubble.reader.bean.IChapter;

/**
 * @author Bubble
 * @date 2020/7/16
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc TODO
 */
public interface OnCacheChapterLoadListener<T extends IChapter> {
    /**
     * 加载缓存
     *
     * @param listener 回调给工厂内部
     */
    void onLoadCache(OnCacheChapterListener<T> listener);
}
