package com.bubble.reader.chapter.listener;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 章节请求接口 由外部实现 再通过{@link OnChapterResultListener}回调给章节工程内部
 */
public interface OnChapterRequestListener {
    /**
     * 请求数据
     *
     * @param isPrepare    是否预加载章节 true 是 false 否
     * @param currentIndex 当前章节的下标
     * @param listener     结果回调 回调给{@link com.bubble.reader.page.OnlinePageCreator} 进行处理
     */
    void onRequest(boolean isPrepare, int currentIndex, OnChapterResultListener listener);
}
