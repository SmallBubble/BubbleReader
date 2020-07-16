package com.bubble.reader.chapter.listener;

/**
 * @author Bubble
 * @date 2020/7/13
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc TODO
 */
public abstract class OnChapterListener {
    public final static int TYPE_INIT = 0x0001;
    public final static int TYPE_LOADED = 0x0002;
    public final static int TYPE_COMPLETE = 0x0003;
    public final static int TYPE_ERROR = 0x0004;
    public final static int TYPE_PREPARE_LOAD = 0x0005;

    /**
     * 初始化完成 打开阅读器后的第一章解析完成
     */
    public void onInitialized() {

    }

    /**
     * 章节加载完成
     */
    public void onChapterLoaded() {

    }

    /**
     * 解析完成
     */
    public void onComplete() {

    }

    /**
     * 解析错误
     *
     * @param e
     */
    public void onError(Throwable e) {

    }

    /**
     * 预加载完成
     */
    public void onPrepareComplete() {

    }
}