package com.bubble.reader.bean;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class ChapterBean implements IChapter {
    public String mChapterName;
    public String mChapterContent;
    private int mChapterNo;
    private int mChapterCount;
    private boolean mBookStart;
    private boolean mBookEnd;


    @Override
    public String getChapterName() {
        return mChapterName;
    }

    @Override
    public int getChapterNo() {
        return mChapterNo;
    }

    @Override
    public int getChapterCount() {
        return mChapterCount;
    }

    @Override
    public boolean isBookStart() {
        return mBookStart;
    }

    @Override
    public boolean isBookEnd() {
        return mBookEnd;
    }

    @Override
    public String getContent() {
        return mChapterContent;
    }

    public void setChapterName(String chapterName) {
        mChapterName = chapterName;
    }

    public String getChapterContent() {
        return mChapterContent;
    }

    public void setChapterContent(String chapterContent) {
        mChapterContent = chapterContent;
    }

    public void setChapterNo(int chapterNo) {
        mChapterNo = chapterNo;
    }

    public void setChapterCount(int chapterCount) {
        mChapterCount = chapterCount;
    }

    public void setBookStart(boolean bookStart) {
        mBookStart = bookStart;
    }

    public void setBookEnd(boolean bookEnd) {
        mBookEnd = bookEnd;
    }

    @Override
    public String toString() {
        return "ChapterBean{" +
                "mChapterName='" + mChapterName + '\'' +
                ", mChapterContent='" + mChapterContent + '\'' +
                ", mChapterNo=" + mChapterNo +
                ", mChapterCount=" + mChapterCount +
                ", mBookStart=" + mBookStart +
                ", mBookEnd=" + mBookEnd +
                '}';
    }
}