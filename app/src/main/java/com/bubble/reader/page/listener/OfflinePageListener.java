package com.bubble.reader.page.listener;

/**
 * packger：com.bubble.reader.page.event
 * auther：Bubble
 * date：2020/6/30
 * email：jiaxiang6595@foxmail.com
 * Desc：
 */
public abstract class OfflinePageListener extends PageListener {
    /**
     * 找不到文件
     */
    public abstract void onFileNotFound();

}
