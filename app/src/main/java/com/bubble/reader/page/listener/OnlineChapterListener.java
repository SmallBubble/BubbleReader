package com.bubble.reader.page.listener;

import com.bubble.reader.page.bean.IChapter;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public interface OnlineChapterListener<T extends IChapter> {
    void onGetChapterSuccess(boolean isPrepare, T chapter);

    void onGetChapterFailure(String message);
}
