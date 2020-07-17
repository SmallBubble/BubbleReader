package com.bubble.breader.bean;

public class TxtChapter extends Chapter {

    private int mChapterStart;
    private int mChapterEnd;

    public int getChapterStart() {
        return mChapterStart;
    }

    public void setChapterStart(int chapterStart) {
        mChapterStart = chapterStart;
    }

    public int getChapterEnd() {
        return mChapterEnd;
    }

    public void setChapterEnd(int chapterEnd) {
        mChapterEnd = chapterEnd;
    }


    @Override
    public String toString() {
        return "TxtChapter{" +
                "mChapterStart=" + mChapterStart +
                ", mChapterEnd=" + mChapterEnd +
                ", mChapterName='" + mChapterName + '\'' +
                ", mChapterContent='" + mChapterContent + '\'' +
                '}';
    }
}
