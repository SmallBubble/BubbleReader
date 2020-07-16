package com.bubble.reader.chapter.loader;

import com.bubble.reader.bean.IChapter;

/**
 * @author Bubble
 * @date 2020/7/16
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 章节加载器 加载单章章节
 */
public interface ChapterLoader<T extends IChapter> extends Loader<T> {
    /**
     * 根据章节号 加载章节
     *
     * @param isPrepare 是否预加载
     * @param needNo    需要加载的章节号
     * @param result    结果 回调给工厂内部处理
     */
    void loadChapter(boolean isPrepare, int needNo, ChapterResult<T> result);

    interface ChapterResult<T> {
        void onResult(boolean isPrepare, T chapter);
    }
}
