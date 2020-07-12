package com.bubble.reader.page.bean;

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
    private int mChapterIndex;

    private boolean mBookStart;
    private boolean mBookEnd;


    @Override
    public String getChapterName() {
        return mChapterName;
    }

    @Override
    public int getChapterNo() {
        return 0;
    }

    @Override
    public int getChapterCount() {
        return 0;
    }

    @Override
    public boolean isBookStart() {
        return false;
    }

    @Override
    public boolean isBookEnd() {
        return false;
    }

    @Override
    public String getContent() {
        return mChapterContent;
    }
}