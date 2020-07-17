package com.bubble.breader.chapter;

import com.bubble.breader.bean.IChapter;

import java.util.Map;

/**
 * @author Bubble
 * @date 2020/7/14
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc TODO
 */
public interface IChapterFactory<T extends IChapter> {
    /**
     * 初始化工厂
     */
    void init();

    /**
     * 获取当前章编号
     *
     * @return
     */
    int getCurrentChapterNo();

    /**
     * 获取当前章内容
     *
     * @return
     */
    String getCurrentContent();

    /**
     * 获取当前章节名称
     *
     * @return
     */
    String getCurrentName();

    /**
     * 获取章节数量
     *
     * @return
     */
    int getChapterCount();

    /**
     * 获取已经加载的章节数量
     *
     * @return
     */
    int getLoadedChapterCount();

    /**
     * 获取已经加载的所有章节
     *
     * @return
     */
    Map<String, T> getLoadedChapters();

    /**
     * 回收资源
     */
    void recycle();

    /**
     * 编码
     *
     * @return
     */
    String getEncoding();

    String getBookName();

    /**
     * 是否到书记纪委
     *
     * @return
     */
    boolean isBookEnd();

    /**
     * 是否到书籍开始
     *
     * @return
     */
    boolean isStart();

    /**
     * 加载章节
     *
     * @param isNext
     */
    boolean loadChapter(boolean isNext);

    /**
     * 加載本章章節
     */
    void loadChapter();
}
