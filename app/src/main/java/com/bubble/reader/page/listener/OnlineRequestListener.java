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
    void getNextChapter(int currentIndex, OnlineChapterListener listener);

    void getPreChapter(int currentIndex, OnlineChapterListener listener);
}
