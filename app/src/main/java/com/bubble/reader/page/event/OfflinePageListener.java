package com.bubble.reader.page.event;

/**
 * packger：com.bubble.reader.page.event
 * auther：Bubble
 * date：2020/6/30
 * email：jiaxiang6595@foxmail.com
 * Desc：
 */
public interface OfflinePageListener extends PageListener {
    /**
     * 找不到文件
     */
    void onFileNotFound();
}
