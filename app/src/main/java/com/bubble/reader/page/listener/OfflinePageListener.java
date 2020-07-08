package com.bubble.reader.page.listener;

/**
 * packger：com.bubble.reader.page.event
 * author：Bubble
 * date：2020/6/30
 * email：1337986595@qq.com
 * Desc：
 */
public abstract class OfflinePageListener extends PageListener {
    /**
     * 找不到文件
     */
    public abstract void onFileNotFound();

}
