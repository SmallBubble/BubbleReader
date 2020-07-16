package com.bubble.reader.chapter.listener;

import com.bubble.reader.bean.IChapter;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 章节请求接口 由外部实现 再通过{@link OnChapterResultListener}回调给章节工程内部
 */
public interface OnChapterRequestListener<T extends IChapter> {
    /**
     * 请求数据
     *
     * @param isPrepare    是否预加载章节 true 是 false 否
     * @param needIndex 当前章节的下标
     * @param listener
     */
    void onRequest(boolean isPrepare, int needIndex, OnChapterResultListener<T> listener);
}
