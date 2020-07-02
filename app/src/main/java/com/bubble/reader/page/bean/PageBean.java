package com.bubble.reader.page.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * packger：com.bubble.reader.page.bean
 * auther：Bubble
 * date：2020/6/21
 * email：jiaxiang6595@foxmail.com
 * Desc：
 */
public class PageBean {
    /**
     * 章节名称
     */
    private String mChapterName;
    /**
     * 是否是书籍开始和结束
     */
    private boolean mBookStart;
    private boolean mBookEnd;
    /**
     * 页开始位置 和结束位置
     */
    private long mPageStart;
    private long mPageEnd;
    /**
     * 章节中的页码
     */
    private int mChapterPage;
    /**
     * 章节内容
     */
    private List<String> mContent;

    public PageBean() {
        mContent = new ArrayList<>();
    }

    public String getChapterName() {
        return mChapterName;
    }

    public void setChapterName(String chapterName) {
        mChapterName = chapterName;
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

    public long getPageStart() {
        return mPageStart;
    }

    public void setPageStart(long pageStart) {
        mPageStart = pageStart;
    }

    public long getPageEnd() {
        return mPageEnd;
    }

    public void setPageEnd(long pageEnd) {
        mPageEnd = pageEnd;
    }

    public int getChapterPage() {
        return mChapterPage;
    }

    public void setChapterPage(int chapterPage) {
        mChapterPage = chapterPage;
    }

    public List<String> getContent() {
        return mContent;
    }

    public void setContent(List<String> content) {
        mContent = content;
    }
}
