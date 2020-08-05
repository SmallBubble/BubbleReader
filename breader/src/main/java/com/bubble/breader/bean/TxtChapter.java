package com.bubble.breader.bean;

/**
 * @author Bubble
 * @date 2020/8/5
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc Txt文件 新增两个字段 章节开始位置 章节结束位置（在文件流中的位置）
 */
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
