package com.bubble.reader.chapter.loader;

import com.bubble.reader.bean.IChapter;

import java.util.Map;

/**
 * @author Bubble
 * @date 2020/7/16
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 章节加载器 加载全部章节
 */
public interface ChaptersLoader<T extends IChapter> extends Loader<T> {
    /**
     * 加载缓存
     *
     * @param result 结果
     */
    void loadCache(ChaptersResult<T> result);

    interface ChaptersResult<T> {
        void onResult(Map<String, T> chapters);
    }
}
