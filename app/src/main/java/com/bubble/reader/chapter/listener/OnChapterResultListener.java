package com.bubble.reader.chapter.listener;

import com.bubble.reader.bean.IChapter;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 章节请求结果 在{@link OnChapterRequestListener#onRequest(boolean, int, OnChapterResultListener)} 中获取到内容后回调个章节工厂
 */
public interface OnChapterResultListener<T extends IChapter> {
    /**
     * 获取章节成功
     *
     * @param isPrepare
     * @param chapter
     */
    void onGetChapterSuccess(boolean isPrepare, T chapter);

    /**
     * 获取章节失败
     *
     * @param message
     */
    void onGetChapterFailure(String message);
}
