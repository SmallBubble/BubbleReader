package com.bubble.reader.chapter;

import com.bubble.reader.bean.IChapter;

import java.util.Map;

interface IChapterFactory<T extends IChapter> {
    int getCurrentChapterNo();

    int getChapterCount();

    int getLoadedChapterCount();

    Map<String, T> getLoadedChapters();

    void recycle();

    String getEncoding();
}
