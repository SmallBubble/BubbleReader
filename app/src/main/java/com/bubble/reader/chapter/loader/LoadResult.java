package com.bubble.reader.chapter.loader;

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
public interface LoadResult<T extends IChapter> {
    void onResult(T chapter);

    void onResult(Map<String, T> chapters);
}
