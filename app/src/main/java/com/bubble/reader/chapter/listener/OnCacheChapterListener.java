package com.bubble.reader.chapter.listener;

import com.bubble.reader.bean.IChapter;

import java.util.Map;

/**
 * @author Bubble
 * @date 2020/7/16
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc TODO
 */
public interface OnCacheChapterListener<T extends IChapter> {
    /**
     * 加载缓存
     *
     * @param chapters
     */
    void onLoadCache(Map<String, T> chapters);
}
