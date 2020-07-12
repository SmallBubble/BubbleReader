package com.bubble.reader.page.bean;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class ChapterBean {
    public String mChapterName;
    private int mChapterIndex;

    private boolean mBookStart;
    private boolean mBookEnd;

    public ChapterBean(String chapterName, int chapterIndex, boolean bookStart, boolean bookEnd) {
        mChapterName = chapterName;
        mChapterIndex = chapterIndex;
        mBookStart = bookStart;
        mBookEnd = bookEnd;
    }

    public ChapterBean(String chapterName, int chapterIndex) {
        mChapterName = chapterName;
        mChapterIndex = chapterIndex;
    }

    public boolean isBookStart() {
        return mBookStart;
    }

    public void setBookStart(boolean bookStart) {
        mBookStart = bookStart;
    }

    public boolean isBookEnd() {
        return mBookEnd;
    }

    public void setBookEnd(boolean bookEnd) {
        mBookEnd = bookEnd;
    }

    public String getChapterName() {
        return mChapterName;
    }

    public void setChapterName(String chapterName) {
        mChapterName = chapterName;
    }

    public int getChapterIndex() {
        return mChapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        mChapterIndex = chapterIndex;
    }
}
