package com.bubble.reader.page.listener;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public interface OnlineRequestListener {
    /**
     * 请求数据
     *
     * @param isPrepare    是否预加载章节 true 是 false 否
     * @param currentIndex 当前章节的下标
     * @param listener     结果回调 回调给{@link com.bubble.reader.page.OnlinePageCreator} 进行处理
     */
    void onRequest(boolean isPrepare, int currentIndex, OnlineChapterListener listener);
}
