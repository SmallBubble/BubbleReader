package com.bubble.reader.chapter;

import com.bubble.reader.bean.IChapter;

import java.util.Map;

interface IChapterFactory<T extends IChapter> {
    /**
     * 初始化工厂
     */
    void initData();

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

    /**
     * 是否到书记纪委
     *
     * @return
     */
    boolean isEnd();

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
    void onLoadChapter(boolean isNext);
}
